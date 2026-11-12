package com.sena.productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.productos.exception.ProductoNoEncontradoException;
import com.sena.productos.model.Producto;
import com.sena.productos.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class) // solo carga el controller (+ el manejador de excepciones)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc; // "Postman" de mentiras para probar el controller

    @Autowired
    private ObjectMapper objectMapper; // convierte objetos Java a JSON

    @MockBean
    private ProductoService productoService; // service falso

    // ---------- GET /api/productos ----------

    @Test
    @DisplayName("GET /api/productos: devuelve 200 y la lista")
    void listar_deberiaRetornar200() throws Exception {
        // Arrange
        when(productoService.listar()).thenReturn(Arrays.asList(
                new Producto(1L, "Mouse", 25000.0, 10),
                new Producto(2L, "Teclado", 60000.0, 5)));

        // Act + Assert
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Mouse"));
    }

    // ---------- GET /api/productos/{id} ----------

    @Test
    @DisplayName("GET /api/productos/1: devuelve 200 y el producto")
    void buscarPorId_cuandoExiste_deberiaRetornar200() throws Exception {
        // Arrange
        when(productoService.buscarPorId(1L))
                .thenReturn(new Producto(1L, "Mouse", 25000.0, 10));

        // Act + Assert
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    @Test
    @DisplayName("GET /api/productos/99: devuelve 404 si no existe")
    void buscarPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        // Arrange: el service falso lanza la excepción
        when(productoService.buscarPorId(99L))
                .thenThrow(new ProductoNoEncontradoException(99L));

        // Act + Assert
        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró el producto con id 99"));
    }

    // ---------- POST /api/productos ----------

    @Test
    @DisplayName("POST /api/productos: con datos válidos devuelve 201")
    void crear_conDatosValidos_deberiaRetornar201() throws Exception {
        // Arrange
        Producto entrada = new Producto(null, "Monitor", 450000.0, 3);
        Producto salida = new Producto(1L, "Monitor", 450000.0, 3);
        when(productoService.crear(any(Producto.class))).thenReturn(salida);

        // Act + Assert
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Monitor"));
    }

    @Test
    @DisplayName("POST /api/productos: nombre vacío devuelve 400")
    void crear_conNombreVacio_deberiaRetornar400() throws Exception {
        // Arrange: producto con el nombre en blanco
        Producto invalido = new Producto(null, "", 1000.0, 5);

        // Act + Assert
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));

        // El service ni siquiera debió ser llamado
        verify(productoService, never()).crear(any());
    }

    @Test
    @DisplayName("POST /api/productos: precio negativo devuelve 400")
    void crear_conPrecioNegativo_deberiaRetornar400() throws Exception {
        // Arrange
        Producto invalido = new Producto(null, "Cable", -500.0, 5);

        // Act + Assert
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precio").value("El precio debe ser mayor a 0"));

        verify(productoService, never()).crear(any());
    }

    // ---------- PUT /api/productos/{id} ----------

    @Test
    @DisplayName("PUT /api/productos/1: actualiza y devuelve 200")
    void actualizar_conDatosValidos_deberiaRetornar200() throws Exception {
        // Arrange
        Producto entrada = new Producto(null, "Mouse Gamer", 80000.0, 7);
        Producto salida = new Producto(1L, "Mouse Gamer", 80000.0, 7);
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(salida);

        // Act + Assert
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));
    }

    // ---------- DELETE /api/productos/{id} ----------

    @Test
    @DisplayName("DELETE /api/productos/1: devuelve 204")
    void eliminar_cuandoExiste_deberiaRetornar204() throws Exception {
        // Arrange: nada, el service falso no hace nada por defecto

        // Act + Assert
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE /api/productos/99: devuelve 404 si no existe")
    void eliminar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        // Arrange: el service falso lanza excepción al eliminar
        doThrow(new ProductoNoEncontradoException(99L))
                .when(productoService).eliminar(99L);

        // Act + Assert
        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound());
    }
}
