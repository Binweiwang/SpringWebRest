package com.example.springwebrest.funko.services;

import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.categoria.repository.CategoriaRepository;
import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.funko.exceptions.FunkoBadRequest;
import com.example.springwebrest.funko.exceptions.FunkoNotFound;
import com.example.springwebrest.funko.mapper.FunkoMapper;
import com.example.springwebrest.funko.models.Funko;
import com.example.springwebrest.funko.repository.FunkoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FunkoServicesImp implements FunkoServices {
    private final FunkoRepository funkoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FunkoMapper funkoMapper;

    @Autowired
    public FunkoServicesImp(FunkoRepository funkoRepository, CategoriaRepository categoriaRepository, FunkoMapper funkoMapper) {
        this.funkoRepository = funkoRepository;
        this.categoriaRepository = categoriaRepository;
        this.funkoMapper = funkoMapper;
    }


    @Override
    public List<Funko> findAll() {
        return funkoRepository.findAll();
    }

    @Override
    public FunkoResponseDto findById(Long id) {
        return  funkoMapper.toFunko(funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe")));
    }

    @Override
    public FunkoResponseDto save(FunkoCreateRequest funko) {
        var categoria = checkCategoria(funko.getCategoria());
        var funkoToSave = funkoRepository.save(funkoMapper.toFunko(funko, categoria));
        return funkoMapper.toFunko(funkoToSave);
    }

    @Override
    public FunkoResponseDto update(Long id , FunkoUpdateRequest funko) {
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound("El funko con id " + id + " no existe"));
        Categoria categoria = null;

        if(funko.getCategoria() != null && !funko.getCategoria().isEmpty()){
            categoria = checkCategoria(funko.getCategoria());
        } else{
            categoria = funkoActual.getCategoria();
        }
        var funkoToUpdate = funkoRepository.save(funkoMapper.toFunko(funko,funkoActual, categoria));
        return funkoMapper.toFunko(funkoToUpdate);
    }


    @Override
    public void deleteById(Long id) {
        funkoRepository.findById(id).orElseThrow(()-> new FunkoNotFound("El funko con id " + id + " no existe"));
        funkoRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        funkoRepository.deleteAll();
    }
    protected Categoria checkCategoria(String nombreCategoria) {
        var categoria = categoriaRepository.findByTipoEqualsIgnoreCase(nombreCategoria);
        // Buscamos la categoría por su nombre, debe existir y no estar borrada
        if (categoria.isEmpty() || categoria.get().isActive()) {
            throw new FunkoBadRequest("La categoría " + nombreCategoria + " no existe o está borrada");
        }
        return categoria.get();
    }
}
