package com.roles.usermanagement.modules.customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service @Transactional
public class CustomerService {

 private final CustomerRepository repository;

 public CustomerService(CustomerRepository repository){
  this.repository=repository;
 }

 private Customer existing(Long id,boolean lock){
  return (lock?repository.findForUpdate(id):repository.findById(id))
          .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));
 }

 private CustomerResponse dto(Customer e){
  return new CustomerResponse(e.getId(),e.getName(),e.getEmail(),e.getPhone(),e.isActive());
 }

 @Transactional(readOnly=true)
 public Page<CustomerResponse> all(int page,int size){
  if(page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"page >= 0; size entre 1 y 100");
  return repository.findAll(PageRequest.of(page,size,Sort.by("id"))).map(this::dto);
 }

 @Transactional(readOnly=true)
 public CustomerResponse get(Long id){
  return dto(existing(id,false));
 }

 public CustomerResponse create(CustomerRequest d){
  Customer e=new Customer(); e.setName(d.name().trim()); e.setEmail(d.email().trim()); e.setPhone(d.phone());
  return dto(repository.save(e));
 }

 public CustomerResponse update(Long id,CustomerRequest d){
  Customer e=existing(id,true);
  if(!e.isActive())throw new ResponseStatusException(HttpStatus.CONFLICT,"Registro inactivo"); e.setName(d.name().trim()); e.setEmail(d.email().trim()); e.setPhone(d.phone());
  return dto(e);
 }

 public void deactivate(Long id){
  existing(id,true).setActive(false);
 }
}
