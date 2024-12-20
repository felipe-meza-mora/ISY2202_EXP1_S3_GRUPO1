package com.example.sum1.service;

import com.example.sum1.exception.RecetaNotFoundException;
import com.example.sum1.model.Receta;
import com.example.sum1.repository.RecetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;


    public RecetaServiceImpl(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    // Obtener todas las recetas
    @Override
    public List<Receta> getAllRecetas() {
        return recetaRepository.findAll();
    }
    
    // Crear una nueva receta
    @Override
    public Receta saveReceta(Receta receta) {
        return recetaRepository.save(receta);
    }
    
    // Obtener una receta por su ID
    @Override
    public Receta getRecetaById(Long id) {
        Optional<Receta> receta = recetaRepository.findById(id);
        if (receta.isPresent()) {
            return receta.get();
        } else {
            throw new RecetaNotFoundException("Receta no encontrada con el ID: " + id);
        }
    }
    
    // Actualizar una receta
    @Override
    public Receta updateReceta(Long id, Receta recetaDetails) {
        Receta receta = getRecetaById(id);
        receta.setTitulo(recetaDetails.getTitulo());
        receta.setDescripcion(recetaDetails.getDescripcion());
        receta.setImagenUrl(recetaDetails.getImagenUrl());
        receta.setTiempoPreparacion(recetaDetails.getTiempoPreparacion());
        receta.setPasos(recetaDetails.getPasos());
        receta.setComentarios(recetaDetails.getComentarios());
        receta.setValoraciones(recetaDetails.getValoraciones());
        return recetaRepository.save(receta);
    }
    
    // eliminar receta
    @Override
    public void deleteReceta(Long id) {
        Receta receta = getRecetaById(id);
        recetaRepository.delete(receta);
    }
}