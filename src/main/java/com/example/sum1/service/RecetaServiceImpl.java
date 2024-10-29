package com.example.sum1.service;

import com.example.sum1.model.Receta;
import com.example.sum1.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetaServiceImpl implements RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;
    
    // Obtener todas las recetas
    @Override
    public List<Receta> getAllRecetas() {
        List<Receta> recetas = recetaRepository.findAll();
        System.out.println("Cantidad de recetas obtenidas: " + recetas.size());
        recetas.forEach(r -> System.out.println("Receta: " + r.getTitulo()));
        return recetas;
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
            throw new RuntimeException("Receta no encontrada con el ID: " + id);
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
        return recetaRepository.save(receta);
    }
    
    // Obtener todos los productos
    @Override
    public void deleteReceta(Long id) {
        Receta receta = getRecetaById(id);
        recetaRepository.delete(receta);
    }
}