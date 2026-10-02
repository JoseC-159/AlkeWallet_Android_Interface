# AlkeWallet - Aplicación Android de Billetera Digital
**AlkeWallet** es una aplicación móvil nativa para Android desarrollada en **Kotlin**, diseñada para simular la experiencia completa de una billetera digital moderna (*Fintech*). Permite a los usuarios gestionar su saldo, realizar transferencias entre contactos, ingresar dinero, consultar un historial de transacciones en tiempo real, recibir notificaciones y convertir divisas dinámicamente con tasas de cambio actualizadas.

## Características Principales

* **Autenticación y Gestión de Sesión**:
  * Pantallas de registro (*Signup*) e inicio de sesión (*Login*) con validación de credenciales.
  * Persistencia de sesión automática mediante base de datos local (**Room / SQLite**).

* **Panel Principal (*Home Dashboard*)**:
  * Visualización clara del balance total en la divisa seleccionada.
  * Accesos rápidos para **Enviar dinero** e **Ingresar dinero**.
  * Historial dinámico de transacciones recientes con estado vacío (*Empty State*) en caso de no registrar movimientos.
  * Sistema de notificaciones en tiempo real con indicador visual de alerta (*Campanita*) y menú emergente (*Popup*).

* **Operaciones Financieras**:
  * **Envío de Dinero**: Selección de destinatario entre cuentas registradas, monto y motivo de transferencia.
  * **Ingreso de Dinero**: Depósito de fondos a la cuenta propia con notas personalizadas.
  * Actualización automática del saldo y registro automático en el historial de transacciones.

* **Perfil de Usuario y Ajustes (*Profile*)**:
  * Muestra los datos del usuario activo (nombre completo, correo electrónico).
  * **Conversor de Moneda en Tiempo Real**: Cambio de divisa entre **CLP**, **USD** y **EUR** consultando tasas de cambio mediante una **API REST (Retrofit)**, con mecanismo de respaldo (*fallback*) offline.
  * Secciones desplegables (*Acordeones*) para consultar información personal, tarjetas asociadas y centro de ayuda.
  * Cierre de sesión seguro.

---

## Arquitectura y Tecnologías Utilizadas

La aplicación sigue una arquitectura limpia basada en la evolución de patrones **MVC a MVVM**, garantizando la separación de responsabilidades y la mantenibilidad del código:

* **Lenguaje**: Kotlin 100% nativo.
* **UI & Layouts**: Material Design Components, ConstraintLayout, CardView, RecyclerView, ViewBinding.
* **Persistencia Local**: **Room Database (SQLite)** para el almacenamiento local de usuarios, cuentas y transacciones.
* **Red / Consumo de APIs**: **Retrofit 2** + **Gson Converter** para la consulta de tipos de cambio de divisas.
* **Carga de Imágenes**: **Picasso** para la carga eficiente y gestión en memoria de imágenes/avatares.
* **Asincronía**: **Kotlin Coroutines** y `lifecycleScope` para operaciones de base de datos y red fuera del hilo principal (*UI Thread*).
* **Testing**: Pruebas unitarias integradas (**JUnit 4**) para probar la lógica de negocio y controladores (*WalletControllerTest*).

---

## Estructura del Proyecto

```text
com.mcu.alkewalletinterface/
├── data/
│   ├── local/          # Base de datos Room (Entities, DAOs, DbHelper)
│   └── remote/         # Retrofit Client e Interfaces de API REST
├── model/              # Modelos de datos (User, Cuenta, Transaccion)
├── controller/         # Lógica de negocio y adaptadores (WalletController, TransactionAdapter)
└── view/               # Actividades y Fragmentos (Splash, Login, Home, Profile, SendMoney, etc.)
```

---

## Pruebas Unitarias

El proyecto incluye pruebas unitarias para validar las reglas de negocio principales (balance inicial, recepción de fondos, conversión de divisas):

```bash
./gradlew app:testDebugUnitTest
```
