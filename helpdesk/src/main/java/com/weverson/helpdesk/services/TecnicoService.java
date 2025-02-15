package com.weverson.helpdesk.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weverson.helpdesk.domain.Pessoa;
import com.weverson.helpdesk.domain.Tecnico;
import com.weverson.helpdesk.domain.dtos.TecnicoDTO;
import com.weverson.helpdesk.repositories.PessoaRepository;
import com.weverson.helpdesk.repositories.TecnicoRepository;
import com.weverson.helpdesk.services.exception.DataIntegrityViolationException;
import com.weverson.helpdesk.services.exception.ObjectNotFoundException;

import jakarta.validation.Valid;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

	public Tecnico findById(Integer id) {

		Optional<Tecnico> obj = repository.findById(id);

		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! id=" + id));

	}

	public List<Tecnico> findAll() {

		return repository.findAll();

	}

	public Tecnico create(TecnicoDTO tecnicoDTO) {
		tecnicoDTO.setId(null);
		validarPorCpfEEmail(tecnicoDTO);
		Tecnico tecnico = new Tecnico(tecnicoDTO);
		return repository.save(tecnico);
	}

	public void validarPorCpfEEmail(TecnicoDTO tecnicoDTO) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(tecnicoDTO.getCpf());
		if (obj.isPresent() && obj.get().getId() != tecnicoDTO.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
		}

		obj = pessoaRepository.findByEmail(tecnicoDTO.getEmail());

		if (obj.isPresent() && obj.get().getId() != tecnicoDTO.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema.");
		}
	}

	public Tecnico update(Integer id, @Valid TecnicoDTO objDTO) {

		objDTO.setId(id);
		Tecnico oldObj = findById(id);
		validarPorCpfEEmail(objDTO);
		oldObj = new Tecnico(objDTO);
		return pessoaRepository.save(oldObj);
	}

	public void delete(Integer id) {
		Tecnico tecnico = findById(id);

		if (tecnico.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Técnico possui ordens de serviço e não pode ser deletado!");
		}
		repository.deleteById(id);
	}

}
