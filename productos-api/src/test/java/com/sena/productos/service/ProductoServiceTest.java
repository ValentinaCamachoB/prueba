package com.sena.productos.service;

import com.sena.productos.exception.ProductoNoEncontradoException;
import com.sena.productos.model.Producto;
import com.sena.productos.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // activa Mockito en esta clase
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository; // repository falso

    @InjectMocks
    private ProductoService productoService; // service real con el repository falso adentro

    // ---------- LISTAR ----------

    @Test
    @DisplayName("Listar: devuelve todos los productos")
    void listar_deberiaRetornarListaDeProductos() {
        // Arrange: el repository falso devuelve 2 productos
        List<Producto> lista = Arrays.asList(
                new Producto(1L, "Mouse", 25000.0, 10),
                new Producto(2L, "Teclado", 60000.0, 5));
        when(productoRepository.findAll()).thenReturn(lista);

        // Act
        List<Producto> resultado = productoService.listar();

        // Assert
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    // ---------- BUSCAR POR ID ----------

    @Test
    @DisplayName("Buscar por id: caso exitoso, el producto existe")
    void buscarPorId_cuandoExiste_deberiaRetornarProducto() {
        // Arrange
        Producto producto = new Producto(1L, "Mouse", 25000.0, 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Producto resultado = productoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Mouse", resultado.getNombre());
    }

    @Test
    @DisplayName("Buscar por id: error, el producto NO existe")
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        // Arrange: el repository dice "no hay nada con ese id"
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert: esperamos que lance la excepción
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.buscarPorId(99L));
    }

    // ---------- CREAR ----------

    @Test
    @DisplayName("Crear: guarda el producto y lo devuelve")
    void crear_deberiaGuardarProducto() {
        // Arrange
        Producto nuevo = new Producto(null, "Monitor", 450000.0, 3);
        Producto guardado = new Producto(1L, "Monitor", 450000.0, 3);
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // Act
        Producto resultado = productoService.crear(nuevo);

        // Assert
        assertEquals(1L, resultado.getId()); // ya trae id
        assertEquals("Monitor", resultado.getNombre());
        verify(productoRepository).save(nuevo);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    @DisplayName("Actualizar: caso exitoso, cambia los datos")
    void actualizar_cuandoExiste_deberiaCambiarDatos() {
        // Arrange
        Producto existente = new Producto(1L, "Mouse", 25000.0, 10);
        Producto datosNuevos = new Producto(null, "Mouse Gamer", 80000.0, 7);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Producto resultado = productoService.actualizar(1L, datosNuevos);

        // Assert
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(80000.0, resultado.getPrecio());
        assertEquals(7, resultado.getStock());
    }

    @Test
    @DisplayName("Actualizar: error, el producto NO existe")
    void actualizar_cuandoNoExiste_deberiaLanzarExcepcion() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        Producto datosNuevos = new Producto(null, "X", 1000.0, 1);

        // Act + Assert
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.actualizar(99L, datosNuevos));

        // Comprobamos que NUNCA se intentó guardar
        verify(productoRepository, never()).save(any());
    }

    // ---------- ELIMINAR ----------

    @Test
    @DisplayName("Eliminar: caso exitoso, borra el producto")
    void eliminar_cuandoExiste_deberiaBorrar() {
        // Arrange
        when(productoRepository.existsById(1L)).thenReturn(true);

        // Act
        productoService.eliminar(1L);

        // Assert: verificamos que sí se llamó a deleteById
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar: error, el producto NO existe")
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        // Arrange
        when(productoRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }
}
