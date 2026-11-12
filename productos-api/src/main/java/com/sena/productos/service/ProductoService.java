package com.sena.productos.service;

import com.sena.productos.exception.ProductoNoEncontradoException;
import com.sena.productos.model.Producto;
import com.sena.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datosNuevos) {
        // Si no existe, buscarPorId lanza la excepción
        Producto existente = buscarPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setPrecio(datosNuevos.getPrecio());
        existente.setStock(datosNuevos.getStock());
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ProductoNoEncontradoException(id);
        }
        productoRepository.deleteById(id);
    }
}
