package com.sena.productos.repository;

import com.sena.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // JpaRepository ya trae save, findAll, findById, deleteById, existsById...
}
