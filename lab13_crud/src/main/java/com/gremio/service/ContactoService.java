package com.gremio.service;

import com.gremio.model.Contacto;
import com.gremio.repository.ContactoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoService {

    private final ContactoRepository repo;

    public ContactoService(ContactoRepository repo) {
        this.repo = repo;
    }

    public List<Contacto> listarTodos() {
        return repo.findAll();
    }

    public void guardar(Contacto contacto) {
        repo.save(contacto);
    }

    public Contacto buscarPorId(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new RuntimeException("Contacto no encontrado: " + id));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
