-- Normalize legacy accounts to one role while preserving their effective permissions.
DO $$
BEGIN
CREATE TABLE IF NOT EXISTS user_role_history (
                                                 username varchar(50) NOT NULL,
    role varchar(50) NOT NULL,
    granted_date timestamp NOT NULL,
    archived_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (username, role, granted_date)
    );

CREATE TABLE IF NOT EXISTS user_permission (
                                               username varchar(50) NOT NULL REFERENCES "user"(username),
    permission_name varchar(50) NOT NULL REFERENCES app_permission(name),
    PRIMARY KEY (username, permission_name)
    );

LOCK TABLE user_role IN SHARE ROW EXCLUSIVE MODE;

    CREATE TEMP TABLE migration_keep_role ON COMMIT DROP AS
SELECT
    username,
    role
FROM (
         SELECT
             username,
             role,
             row_number() OVER (
                PARTITION BY username
                ORDER BY
                    CASE WHEN role = 'ADMIN' THEN 0 ELSE 1 END,
                    granted_date,
                    role
            ) AS position
         FROM user_role
     ) ranked
WHERE position = 1;

INSERT INTO user_role_history (username, role, granted_date)
SELECT
    ur.username,
    ur.role,
    ur.granted_date
FROM user_role ur
         JOIN migration_keep_role kept
              ON kept.username = ur.username
                  AND kept.role <> ur.role
    ON CONFLICT DO NOTHING;

INSERT INTO user_permission (username, permission_name)
SELECT DISTINCT
    ur.username,
    rp.permission_name
FROM user_role ur
         JOIN migration_keep_role kept
              ON kept.username = ur.username
                  AND kept.role <> ur.role
         JOIN role_permission rp
              ON rp.role_name = ur.role
WHERE NOT EXISTS (
    SELECT 1
    FROM role_permission inherited
    WHERE inherited.role_name = kept.role
      AND inherited.permission_name = rp.permission_name
)
    ON CONFLICT DO NOTHING;

DELETE FROM user_role ur
    USING migration_keep_role kept
WHERE ur.username = kept.username
  AND ur.role <> kept.role;

INSERT INTO user_role (username, role, granted_date)
SELECT
    u.username,
    'CUSTOMER',
    CURRENT_TIMESTAMP
FROM "user" u
WHERE NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.username = u.username
);

IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_attribute a
            ON a.attrelid = c.conrelid
           AND a.attnum = c.conkey[1]
        WHERE c.conrelid = to_regclass('user_role')
          AND c.contype = 'u'
          AND cardinality(c.conkey) = 1
          AND a.attname = 'username'
    ) THEN
ALTER TABLE user_role ADD CONSTRAINT uk_user_role_username UNIQUE (username);
END IF;
END $$;