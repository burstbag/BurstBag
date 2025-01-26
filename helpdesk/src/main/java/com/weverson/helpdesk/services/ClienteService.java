package com.weverson.helpdesk.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weverson.helpdesk.domain.Pessoa;
import com.weverson.helpdesk.domain.Cliente;
import com.weverson.helpdesk.domain.dtos.ClienteDTO;
import com.weverson.helpdesk.repositories.PessoaRepository;
import com.weverson.helpdesk.repositories.ClienteRepository;
import com.weverson.helpdesk.services.exception.DataIntegrityViolationException;
import com.weverson.helpdesk.services.exception.ObjectNotFoundException;

import jakarta.validation.Valid;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

	public Cliente findById(Integer id) {

		Optional<Cliente> obj = repository.findById(id);

		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! id=" + id));

	}

	public List<Cliente> findAll() {

		return repository.findAll();

	}

	public Cliente create(ClienteDTO tecnicoDTO) {
		tecnicoDTO.setId(null);
		validarPorCpfEEmail(tecnicoDTO);
		Cliente tecnico = new Cliente(tecnicoDTO);
		return repository.save(tecnico);
	}

	public void validarPorCpfEEmail(ClienteDTO tecnicoDTO) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(tecnicoDTO.getCpf());
		if (obj.isPresent() && obj.get().getId() != tecnicoDTO.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
		}

		obj = pessoaRepository.findByEmail(tecnicoDTO.getEmail());

		if (obj.isPresent() && obj.get().getId() != tecnicoDTO.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema.");
		}
	}

	public Cliente update(Integer id, @Valid ClienteDTO objDTO) {

		objDTO.setId(id);
		Cliente oldObj = findById(id);
		validarPorCpfEEmail(objDTO);
		oldObj = new Cliente(objDTO);
		return pessoaRepository.save(oldObj);
	}

	public void delete(Integer id) {
		Cliente tecnico = findById(id);

		if (tecnico.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Cliente possui ordens de serviço e não pode ser deletado!");
		}
		repository.deleteById(id);
	}


}
