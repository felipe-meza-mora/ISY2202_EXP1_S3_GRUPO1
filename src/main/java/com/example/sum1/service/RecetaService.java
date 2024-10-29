package com.example.sum1.service;

import com.example.sum1.model.Receta;
import java.util.List;

public interface RecetaService {
    List<Receta> getAllRecetas();
    Receta saveReceta(Receta receta);
    Receta getRecetaById(Long id);
    Receta updateReceta(Long id, Receta receta);
    void deleteReceta(Long id);
}