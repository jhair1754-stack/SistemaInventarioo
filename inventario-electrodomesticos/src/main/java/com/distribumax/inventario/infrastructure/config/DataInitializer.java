package com.distribumax.inventario.infrastructure.config;

import com.distribumax.inventario.domain.model.*;
import com.distribumax.inventario.domain.model.enums.EstadoUnidad;
import com.distribumax.inventario.domain.model.enums.TipoMovimiento;
import com.distribumax.inventario.domain.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Inicializador de datos semilla.
 * Carga datos de demostración al arrancar la aplicación
 * si las tablas están vacías.
 *
 * Datos: Categorías, Subcategorías, Marcas, Proveedores,
 *        Almacenes, Ubicaciones, Productos, Unidades y Movimientos.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepo;
    private final SubcategoriaRepository subcategoriaRepo;
    private final MarcaRepository marcaRepo;
    private final ProveedorRepository proveedorRepo;
    private final AlmacenRepository almacenRepo;
    private final UbicacionRepository ubicacionRepo;
    private final ProductoRepository productoRepo;
    private final UnidadProductoRepository unidadRepo;
    private final MovimientoRepository movimientoRepo;

    public DataInitializer(
            CategoriaRepository categoriaRepo,
            SubcategoriaRepository subcategoriaRepo,
            MarcaRepository marcaRepo,
            ProveedorRepository proveedorRepo,
            AlmacenRepository almacenRepo,
            UbicacionRepository ubicacionRepo,
            ProductoRepository productoRepo,
            UnidadProductoRepository unidadRepo,
            MovimientoRepository movimientoRepo) {
        this.categoriaRepo = categoriaRepo;
        this.subcategoriaRepo = subcategoriaRepo;
        this.marcaRepo = marcaRepo;
        this.proveedorRepo = proveedorRepo;
        this.almacenRepo = almacenRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.productoRepo = productoRepo;
        this.unidadRepo = unidadRepo;
        this.movimientoRepo = movimientoRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Solo insertar si la BD está vacía
        if (categoriaRepo.count() > 0) {
            return;
        }

        // ═══════════════════════════════════════════════════
        //  CATEGORÍAS (Dimensión Nivel 1)
        // ═══════════════════════════════════════════════════
        Categoria c1 = new Categoria();
        c1.setNombre("Línea Blanca");
        c1.setDescripcion("Electrodomésticos grandes para el hogar: refrigeradoras, lavadoras, cocinas");
        Categoria lineaBlanca = categoriaRepo.save(c1);

        Categoria c2 = new Categoria();
        c2.setNombre("Línea Marrón");
        c2.setDescripcion("Equipos de entretenimiento: televisores, sistemas de sonido");
        Categoria lineaMarron = categoriaRepo.save(c2);

        Categoria c3 = new Categoria();
        c3.setNombre("Pequeños Electrodomésticos");
        c3.setDescripcion("Electrodomésticos portátiles: licuadoras, planchas, tostadoras");
        Categoria pequenosElectro = categoriaRepo.save(c3);

        // ═══════════════════════════════════════════════════
        //  SUBCATEGORÍAS (Dimensión Nivel 2 — Snowflake)
        // ═══════════════════════════════════════════════════
        Subcategoria sc1 = new Subcategoria();
        sc1.setNombre("Refrigeradoras");
        sc1.setCategoria(lineaBlanca);
        Subcategoria refrigeradoras = subcategoriaRepo.save(sc1);

        Subcategoria sc2 = new Subcategoria();
        sc2.setNombre("Lavadoras");
        sc2.setCategoria(lineaBlanca);
        Subcategoria lavadoras = subcategoriaRepo.save(sc2);

        Subcategoria sc3 = new Subcategoria();
        sc3.setNombre("Cocinas");
        sc3.setCategoria(lineaBlanca);
        Subcategoria cocinas = subcategoriaRepo.save(sc3);

        Subcategoria sc4 = new Subcategoria();
        sc4.setNombre("Televisores");
        sc4.setCategoria(lineaMarron);
        Subcategoria televisores = subcategoriaRepo.save(sc4);

        Subcategoria sc5 = new Subcategoria();
        sc5.setNombre("Sistemas de Sonido");
        sc5.setCategoria(lineaMarron);
        Subcategoria sonido = subcategoriaRepo.save(sc5);

        Subcategoria sc6 = new Subcategoria();
        sc6.setNombre("Licuadoras");
        sc6.setCategoria(pequenosElectro);
        Subcategoria licuadoras = subcategoriaRepo.save(sc6);

        Subcategoria sc7 = new Subcategoria();
        sc7.setNombre("Planchas");
        sc7.setCategoria(pequenosElectro);
        Subcategoria planchas = subcategoriaRepo.save(sc7);

        Subcategoria sc8 = new Subcategoria();
        sc8.setNombre("Microondas");
        sc8.setCategoria(pequenosElectro);
        Subcategoria microondas = subcategoriaRepo.save(sc8);

        // ═══════════════════════════════════════════════════
        //  MARCAS (Dimensión)
        // ═══════════════════════════════════════════════════
        Marca m1 = new Marca();
        m1.setNombre("Samsung");
        m1.setPaisOrigen("Corea del Sur");
        Marca samsung = marcaRepo.save(m1);

        Marca m2 = new Marca();
        m2.setNombre("LG");
        m2.setPaisOrigen("Corea del Sur");
        Marca lg = marcaRepo.save(m2);

        Marca m3 = new Marca();
        m3.setNombre("Bosch");
        m3.setPaisOrigen("Alemania");
        Marca bosch = marcaRepo.save(m3);

        Marca m4 = new Marca();
        m4.setNombre("Whirlpool");
        m4.setPaisOrigen("Estados Unidos");
        Marca whirlpool = marcaRepo.save(m4);

        Marca m5 = new Marca();
        m5.setNombre("Oster");
        m5.setPaisOrigen("Estados Unidos");
        Marca oster = marcaRepo.save(m5);

        // ═══════════════════════════════════════════════════
        //  PROVEEDORES (Dimensión)
        // ═══════════════════════════════════════════════════
        Proveedor p1 = new Proveedor();
        p1.setRuc("20100128056");
        p1.setRazonSocial("Samsung Electronics Perú S.A.C.");
        p1.setContacto("Carlos Méndez");
        p1.setTelefono("01-6125000");
        p1.setEmail("ventas@samsung.pe");
        Proveedor provSamsung = proveedorRepo.save(p1);

        Proveedor p2 = new Proveedor();
        p2.setRuc("20101020312");
        p2.setRazonSocial("LG Electronics Perú S.A.");
        p2.setContacto("María Torres");
        p2.setTelefono("01-6134000");
        p2.setEmail("ventas@lg.pe");
        Proveedor provLG = proveedorRepo.save(p2);

        Proveedor p3 = new Proveedor();
        p3.setRuc("20512345678");
        p3.setRazonSocial("Distribuidora Multihogar S.A.C.");
        p3.setContacto("Jorge Ramírez");
        p3.setTelefono("01-4567890");
        p3.setEmail("compras@multihogar.pe");
        Proveedor provMulti = proveedorRepo.save(p3);

        // ═══════════════════════════════════════════════════
        //  ALMACENES y UBICACIONES (Dimensiones Snowflake)
        // ═══════════════════════════════════════════════════
        Almacen a1 = new Almacen();
        a1.setNombre("Almacén Central Lima");
        a1.setDireccion("Av. Argentina 2458, Cercado de Lima");
        a1.setCiudad("Lima");
        Almacen almCentral = almacenRepo.save(a1);

        Almacen a2 = new Almacen();
        a2.setNombre("Almacén Norte");
        a2.setDireccion("Calle Los Olivos 340, Independencia");
        a2.setCiudad("Lima");
        Almacen almNorte = almacenRepo.save(a2);

        // Ubicaciones del Almacén Central
        Ubicacion u1 = new Ubicacion();
        u1.setPasillo("A"); u1.setEstante("1"); u1.setNivel("1"); u1.setAlmacen(almCentral);
        Ubicacion ubA1 = ubicacionRepo.save(u1);

        Ubicacion u2 = new Ubicacion();
        u2.setPasillo("A"); u2.setEstante("2"); u2.setNivel("1"); u2.setAlmacen(almCentral);
        Ubicacion ubA2 = ubicacionRepo.save(u2);

        Ubicacion u3 = new Ubicacion();
        u3.setPasillo("B"); u3.setEstante("1"); u3.setNivel("1"); u3.setAlmacen(almCentral);
        Ubicacion ubB1 = ubicacionRepo.save(u3);

        Ubicacion u4 = new Ubicacion();
        u4.setPasillo("B"); u4.setEstante("2"); u4.setNivel("2"); u4.setAlmacen(almCentral);
        Ubicacion ubB2 = ubicacionRepo.save(u4);

        Ubicacion u5 = new Ubicacion();
        u5.setPasillo("C"); u5.setEstante("1"); u5.setNivel("1"); u5.setAlmacen(almCentral);
        Ubicacion ubC1 = ubicacionRepo.save(u5);

        // Ubicaciones del Almacén Norte
        Ubicacion u6 = new Ubicacion();
        u6.setPasillo("A"); u6.setEstante("1"); u6.setNivel("1"); u6.setAlmacen(almNorte);
        Ubicacion ubN_A1 = ubicacionRepo.save(u6);

        Ubicacion u7 = new Ubicacion();
        u7.setPasillo("B"); u7.setEstante("1"); u7.setNivel("1"); u7.setAlmacen(almNorte);
        Ubicacion ubN_B1 = ubicacionRepo.save(u7);

        // ═══════════════════════════════════════════════════
        //  PRODUCTOS (Dimensión Central)
        // ═══════════════════════════════════════════════════
        Producto prod1 = new Producto();
        prod1.setCodigoSku("LB-REF-SAM-001");
        prod1.setNombre("Refrigeradora Samsung Side by Side 617L");
        prod1.setModelo("RS64R5311B4");
        prod1.setMarca(samsung);
        prod1.setSubcategoria(refrigeradoras);
        prod1.setPrecioUnitario(new BigDecimal("4299.00"));
        prod1.setPesoKg(new BigDecimal("92.0"));
        prod1.setAnchoCm(new BigDecimal("91.2"));
        prod1.setAltoCm(new BigDecimal("178.0"));
        prod1.setProfundidadCm(new BigDecimal("71.6"));
        prod1.setStockMinimo(3);
        prod1.setStockActual(8);
        prod1.setActivo(true);
        Producto refriSamsung = productoRepo.save(prod1);

        Producto prod2 = new Producto();
        prod2.setCodigoSku("LB-REF-LG-001");
        prod2.setNombre("Refrigeradora LG French Door 22 cu.ft.");
        prod2.setModelo("LM22SGPK");
        prod2.setMarca(lg);
        prod2.setSubcategoria(refrigeradoras);
        prod2.setPrecioUnitario(new BigDecimal("5199.00"));
        prod2.setPesoKg(new BigDecimal("99.0"));
        prod2.setAnchoCm(new BigDecimal("83.5"));
        prod2.setAltoCm(new BigDecimal("178.5"));
        prod2.setProfundidadCm(new BigDecimal("73.0"));
        prod2.setStockMinimo(3);
        prod2.setStockActual(5);
        prod2.setActivo(true);
        Producto refriLG = productoRepo.save(prod2);

        Producto prod3 = new Producto();
        prod3.setCodigoSku("LB-LAV-SAM-001");
        prod3.setNombre("Lavadora Samsung Ecobubble 15kg");
        prod3.setModelo("WA15T5260BY");
        prod3.setMarca(samsung);
        prod3.setSubcategoria(lavadoras);
        prod3.setPrecioUnitario(new BigDecimal("1899.00"));
        prod3.setPesoKg(new BigDecimal("42.0"));
        prod3.setAnchoCm(new BigDecimal("60.0"));
        prod3.setAltoCm(new BigDecimal("101.0"));
        prod3.setProfundidadCm(new BigDecimal("60.0"));
        prod3.setStockMinimo(5);
        prod3.setStockActual(12);
        prod3.setActivo(true);
        Producto lavadoraSamsung = productoRepo.save(prod3);

        Producto prod4 = new Producto();
        prod4.setCodigoSku("LB-LAV-LG-001");
        prod4.setNombre("Lavadora LG Inverter TurboDrum 13kg");
        prod4.setModelo("T2313VSPM");
        prod4.setMarca(lg);
        prod4.setSubcategoria(lavadoras);
        prod4.setPrecioUnitario(new BigDecimal("1699.00"));
        prod4.setPesoKg(new BigDecimal("38.5"));
        prod4.setAnchoCm(new BigDecimal("57.0"));
        prod4.setAltoCm(new BigDecimal("99.0"));
        prod4.setProfundidadCm(new BigDecimal("57.0"));
        prod4.setStockMinimo(5);
        prod4.setStockActual(2);
        prod4.setActivo(true);
        Producto lavadoraLG = productoRepo.save(prod4); // ¡Stock bajo!

        Producto prod5 = new Producto();
        prod5.setCodigoSku("LB-COC-BOS-001");
        prod5.setNombre("Cocina Bosch Empotrable Vitrocerámica 4 hornillas");
        prod5.setModelo("PKE611B17E");
        prod5.setMarca(bosch);
        prod5.setSubcategoria(cocinas);
        prod5.setPrecioUnitario(new BigDecimal("2499.00"));
        prod5.setPesoKg(new BigDecimal("8.5"));
        prod5.setAnchoCm(new BigDecimal("59.2"));
        prod5.setAltoCm(new BigDecimal("5.1"));
        prod5.setProfundidadCm(new BigDecimal("52.2"));
        prod5.setStockMinimo(4);
        prod5.setStockActual(6);
        prod5.setActivo(true);
        Producto cocinaBosch = productoRepo.save(prod5);

        Producto prod6 = new Producto();
        prod6.setCodigoSku("LM-TV-SAM-001");
        prod6.setNombre("Smart TV Samsung Crystal UHD 55\"");
        prod6.setModelo("UN55AU7000GXPE");
        prod6.setMarca(samsung);
        prod6.setSubcategoria(televisores);
        prod6.setPrecioUnitario(new BigDecimal("2199.00"));
        prod6.setPesoKg(new BigDecimal("14.7"));
        prod6.setAnchoCm(new BigDecimal("123.1"));
        prod6.setAltoCm(new BigDecimal("70.7"));
        prod6.setProfundidadCm(new BigDecimal("6.0"));
        prod6.setStockMinimo(8);
        prod6.setStockActual(15);
        prod6.setActivo(true);
        Producto tvSamsung = productoRepo.save(prod6);

        Producto prod7 = new Producto();
        prod7.setCodigoSku("LM-TV-LG-001");
        prod7.setNombre("Smart TV LG NanoCell 65\"");
        prod7.setModelo("65NANO77SRA");
        prod7.setMarca(lg);
        prod7.setSubcategoria(televisores);
        prod7.setPrecioUnitario(new BigDecimal("3799.00"));
        prod7.setPesoKg(new BigDecimal("19.2"));
        prod7.setAnchoCm(new BigDecimal("145.4"));
        prod7.setAltoCm(new BigDecimal("83.4"));
        prod7.setProfundidadCm(new BigDecimal("8.1"));
        prod7.setStockMinimo(5);
        prod7.setStockActual(3);
        prod7.setActivo(true);
        Producto tvLG = productoRepo.save(prod7); // ¡Stock bajo!

        Producto prod8 = new Producto();
        prod8.setCodigoSku("LM-SON-SAM-001");
        prod8.setNombre("Soundbar Samsung HW-B450 2.1 Ch");
        prod8.setModelo("HW-B450/PE");
        prod8.setMarca(samsung);
        prod8.setSubcategoria(sonido);
        prod8.setPrecioUnitario(new BigDecimal("699.00"));
        prod8.setPesoKg(new BigDecimal("6.2"));
        prod8.setAnchoCm(new BigDecimal("86.0"));
        prod8.setAltoCm(new BigDecimal("5.9"));
        prod8.setProfundidadCm(new BigDecimal("10.0"));
        prod8.setStockMinimo(6);
        prod8.setStockActual(10);
        prod8.setActivo(true);
        Producto soundbar = productoRepo.save(prod8);

        Producto prod9 = new Producto();
        prod9.setCodigoSku("PE-LIC-OST-001");
        prod9.setNombre("Licuadora Oster Pro Xpert Series");
        prod9.setModelo("BLSTXP-GPE0-013");
        prod9.setMarca(oster);
        prod9.setSubcategoria(licuadoras);
        prod9.setPrecioUnitario(new BigDecimal("349.00"));
        prod9.setPesoKg(new BigDecimal("4.2"));
        prod9.setAnchoCm(new BigDecimal("22.0"));
        prod9.setAltoCm(new BigDecimal("43.0"));
        prod9.setProfundidadCm(new BigDecimal("22.0"));
        prod9.setStockMinimo(10);
        prod9.setStockActual(25);
        prod9.setActivo(true);
        Producto licuadoraOster = productoRepo.save(prod9);

        Producto prod10 = new Producto();
        prod10.setCodigoSku("PE-PLA-BOS-001");
        prod10.setNombre("Plancha a Vapor Bosch Sensixx'x DA70");
        prod10.setModelo("TDA7030214");
        prod10.setMarca(bosch);
        prod10.setSubcategoria(planchas);
        prod10.setPrecioUnitario(new BigDecimal("289.00"));
        prod10.setPesoKg(new BigDecimal("1.8"));
        prod10.setAnchoCm(new BigDecimal("30.0"));
        prod10.setAltoCm(new BigDecimal("15.0"));
        prod10.setProfundidadCm(new BigDecimal("13.0"));
        prod10.setStockMinimo(8);
        prod10.setStockActual(4);
        prod10.setActivo(true);
        Producto planchaBosch = productoRepo.save(prod10); // ¡Stock bajo!

        Producto prod11 = new Producto();
        prod11.setCodigoSku("PE-MIC-LG-001");
        prod11.setNombre("Microondas LG NeoChef Smart Inverter 32L");
        prod11.setModelo("MS3296OBS");
        prod11.setMarca(lg);
        prod11.setSubcategoria(microondas);
        prod11.setPrecioUnitario(new BigDecimal("549.00"));
        prod11.setPesoKg(new BigDecimal("12.5"));
        prod11.setAnchoCm(new BigDecimal("54.4"));
        prod11.setAltoCm(new BigDecimal("30.6"));
        prod11.setProfundidadCm(new BigDecimal("44.0"));
        prod11.setStockMinimo(6);
        prod11.setStockActual(9);
        prod11.setActivo(true);
        Producto microondasLG = productoRepo.save(prod11);

        Producto prod12 = new Producto();
        prod12.setCodigoSku("PE-MIC-WHI-001");
        prod12.setNombre("Microondas Whirlpool 20L con Grill");
        prod12.setModelo("WM1211D");
        prod12.setMarca(whirlpool);
        prod12.setSubcategoria(microondas);
        prod12.setPrecioUnitario(new BigDecimal("399.00"));
        prod12.setPesoKg(new BigDecimal("11.0"));
        prod12.setAnchoCm(new BigDecimal("45.2"));
        prod12.setAltoCm(new BigDecimal("26.2"));
        prod12.setProfundidadCm(new BigDecimal("35.0"));
        prod12.setStockMinimo(6);
        prod12.setStockActual(1);
        prod12.setActivo(true);
        Producto microondasWhirlpool = productoRepo.save(prod12); // ¡Stock MUY bajo!

        // ═══════════════════════════════════════════════════
        //  UNIDADES CON NÚMERO DE SERIE (para garantías)
        // ═══════════════════════════════════════════════════
        crearUnidades(refriSamsung, ubA1, "RFSAM", 3);
        crearUnidades(tvSamsung, ubB1, "TVSAM", 5);
        crearUnidades(lavadoraSamsung, ubA2, "LVSAM", 4);
        crearUnidades(licuadoraOster, ubC1, "LIOST", 6);

        // ═══════════════════════════════════════════════════
        //  MOVIMIENTOS INICIALES (Tabla de Hechos Snowflake)
        // ═══════════════════════════════════════════════════
        MovimientoInventario mov1 = new MovimientoInventario();
        mov1.setProducto(refriSamsung);
        mov1.setAlmacen(almCentral);
        mov1.setProveedor(provSamsung);
        mov1.setTipoMovimiento(TipoMovimiento.ENTRADA);
        mov1.setCantidad(10);
        mov1.setNumeroLote("LOT-2026-001");
        mov1.setObservaciones("Lote inicial de refrigeradoras Samsung");
        mov1.setFechaMovimiento(LocalDateTime.now().minusDays(15));
        movimientoRepo.save(mov1);

        MovimientoInventario mov2 = new MovimientoInventario();
        mov2.setProducto(tvSamsung);
        mov2.setAlmacen(almCentral);
        mov2.setProveedor(provSamsung);
        mov2.setTipoMovimiento(TipoMovimiento.ENTRADA);
        mov2.setCantidad(20);
        mov2.setNumeroLote("LOT-2026-002");
        mov2.setObservaciones("Lote televisores campaña junio");
        mov2.setFechaMovimiento(LocalDateTime.now().minusDays(10));
        movimientoRepo.save(mov2);

        MovimientoInventario mov3 = new MovimientoInventario();
        mov3.setProducto(tvSamsung);
        mov3.setAlmacen(almCentral);
        mov3.setTipoMovimiento(TipoMovimiento.SALIDA);
        mov3.setCantidad(5);
        mov3.setObservaciones("Venta al por mayor - Cliente: Tiendas Unidas");
        mov3.setFechaMovimiento(LocalDateTime.now().minusDays(5));
        movimientoRepo.save(mov3);

        MovimientoInventario mov4 = new MovimientoInventario();
        mov4.setProducto(lavadoraLG);
        mov4.setAlmacen(almNorte);
        mov4.setProveedor(provLG);
        mov4.setTipoMovimiento(TipoMovimiento.ENTRADA);
        mov4.setCantidad(8);
        mov4.setNumeroLote("LOT-2026-003");
        mov4.setObservaciones("Reposición de lavadoras LG");
        mov4.setFechaMovimiento(LocalDateTime.now().minusDays(3));
        movimientoRepo.save(mov4);

        MovimientoInventario mov5 = new MovimientoInventario();
        mov5.setProducto(lavadoraLG);
        mov5.setAlmacen(almNorte);
        mov5.setTipoMovimiento(TipoMovimiento.SALIDA);
        mov5.setCantidad(6);
        mov5.setObservaciones("Ventas retail semana 24");
        mov5.setFechaMovimiento(LocalDateTime.now().minusDays(1));
        movimientoRepo.save(mov5);

        MovimientoInventario mov6 = new MovimientoInventario();
        mov6.setProducto(licuadoraOster);
        mov6.setAlmacen(almCentral);
        mov6.setProveedor(provMulti);
        mov6.setTipoMovimiento(TipoMovimiento.ENTRADA);
        mov6.setCantidad(30);
        mov6.setNumeroLote("LOT-2026-004");
        mov6.setObservaciones("Lote promocional Oster");
        mov6.setFechaMovimiento(LocalDateTime.now());
        movimientoRepo.save(mov6);

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  ✓ Datos de demostración cargados exitosamente      ║");
        System.out.println("║  → 3 Categorías, 8 Subcategorías, 5 Marcas          ║");
        System.out.println("║  → 3 Proveedores, 2 Almacenes, 7 Ubicaciones        ║");
        System.out.println("║  → 12 Productos, 18 Unidades con serie              ║");
        System.out.println("║  → 6 Movimientos iniciales                          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private void crearUnidades(Producto producto, Ubicacion ubicacion, String prefijo, int cantidad) {
        for (int i = 1; i <= cantidad; i++) {
            UnidadProducto up = new UnidadProducto();
            up.setNumeroSerie(String.format("%s-%06d", prefijo, (producto.getId() * 1000) + i));
            up.setProducto(producto);
            up.setUbicacion(ubicacion);
            up.setEstado(EstadoUnidad.DISPONIBLE);
            up.setFechaIngreso(LocalDate.now().minusDays(15));
            up.setFechaGarantiaFin(LocalDate.now().plusYears(1));
            unidadRepo.save(up);
        }
    }

}

