package com.traxion.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * CLIENTE gRPC ESTÁTICO PARA WEBHOOK TRAXPORTA
 * ============================================
 * 
 * PROPÓSITO:
 * Este cliente permite realizar llamadas gRPC al webhook de Traxporta para registrar placas
 * de vehículos y obtener información de cargas. Está diseñado específicamente para ser
 * invocado desde DataWeave en aplicaciones Mule.
 * 
 * PROBLEMA RESUELTO:
 * DataWeave no puede instanciar objetos Java directamente, por lo que todos los métodos
 * son estáticos. Esto permite llamar las funciones sin crear instancias de la clase.
 * 
 * CONFIGURACIÓN DEL SERVIDOR:
 * - Host: webhook.traxporta.com
 * - Puerto: 443 (HTTPS/TLS)
 * - Protocolo: gRPC con TLS habilitado
 * 
 * ARCHIVOS REQUERIDOS PARA MIGRACIÓN:
 * ===================================
 * 1. ESTE ARCHIVO: StaticRecursoConfiableClient.java
 * 2. STUBS GENERADOS: RecursoConfiableRPCGrpc.java, RecursoConfiable.java
 * 3. ARCHIVO PROTO: recurso_confiable.proto
 * 4. DEPENDENCIAS MAVEN: Ver pom.xml para las librerías gRPC
 * 
 * EJEMPLO DE USO EN NUEVA API:
 * ============================
 * // Llamada básica
 * String response = StaticRecursoConfiableClient.registerPlate("ABC123", "LOAD001", "LN001");
 * 
 * // Desde DataWeave (Mule)
 * %dw 2.0
 * output application/json
 * ---
 * {
 *   result: java!com::traxion::grpc::StaticRecursoConfiableClient::registerPlate("ABC123", "LOAD001", "LN001")
 * }
 * 
 * FORMATOS DE RESPUESTA:
 * =====================
 * - ÉXITO: "WEBHOOK_RESPONSE: {json con datos del webhook}"
 * - ERROR gRPC: "STATIC_gRPC_ERROR: [detalles del error]"
 * - ERROR GENERAL: "STATIC_ERROR: [mensaje de error]"
 * 
 * @author Sistema TMS Unigis
 * @version 1.0
 * @since 2024
 */
public class StaticRecursoConfiableClient {
    
    // ===============================
    // CONFIGURACIÓN Y VARIABLES ESTÁTICAS
    // ===============================
    
    /**
     * CONFIGURACIÓN DEL SERVIDOR WEBHOOK
     * Estos valores apuntan al webhook de producción de Traxporta
     */
    private static final String HOST = "webhook.traxporta.com";  // Servidor del webhook
    private static final int PORT = 443;                         // Puerto HTTPS/TLS
    
    /**
     * INSTANCIA SINGLETON DEL CANAL gRPC
     * Se reutiliza la misma conexión para todas las llamadas (patrón singleton)
     */
    private static volatile ManagedChannel channel;
    
    /**
     * OBJETO DE SINCRONIZACIÓN
     * Evita problemas de concurrencia al crear/cerrar el canal
     */
    private static final Object lock = new Object();
    
    /**
     * LOGGER PARA DEBUGGING
     * Registra todas las operaciones para facilitar el debugging
     */
    private static final Logger logger = Logger.getLogger(StaticRecursoConfiableClient.class.getName());

    // ===============================
    // MÉTODO PRINCIPAL DE CONEXIÓN gRPC
    // ===============================
    
    /**
     * MÉTODO SINGLETON - Obtiene o crea el canal gRPC
     * 
     * Este método implementa el patrón singleton para reutilizar la conexión gRPC.
     * La primera llamada crea el canal, las siguientes reutilizan el existente.
     * 
     * CONFIGURACIÓN DEL CANAL:
     * - TLS habilitado para conexión segura
     * - Keep-alive cada 30 segundos para mantener la conexión
     * - Timeout de keep-alive de 5 segundos
     * - Tamaño máximo de mensaje: 4MB
     * 
     * @return ManagedChannel canal gRPC configurado y listo para usar
     */
    private static ManagedChannel getChannel() {
        // VERIFICAR SI YA EXISTE - Evitar crear múltiples canales
        if (channel == null || channel.isShutdown() || channel.isTerminated()) {
            synchronized (lock) {
                // DOBLE VERIFICACIÓN - Patrón thread-safe singleton
                if (channel == null || channel.isShutdown() || channel.isTerminated()) {
                    logger.info("=== CREANDO NUEVO CANAL gRPC ===");
                    logger.info("Conectando a: " + HOST + ":" + PORT);
                    
                    // CREAR CANAL CON CONFIGURACIÓN COMPLETA
                    channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                            // SEGURIDAD - Habilitar TLS para conexión segura
                            .useTransportSecurity() // Habilita TLS para puerto 443
                            
                            // KEEP-ALIVE - Mantener conexión activa
                            .keepAliveTime(30, TimeUnit.SECONDS)      // Ping cada 30 segundos
                            .keepAliveTimeout(5, TimeUnit.SECONDS)    // Timeout de 5 segundos
                            .keepAliveWithoutCalls(true)              // Keep-alive sin llamadas activas
                            
                            // TAMAÑO DE MENSAJES - Permitir mensajes grandes (4MB)
                            .maxInboundMessageSize(4 * 1024 * 1024)   // 4MB máximo
                            
                            // CONSTRUIR CANAL
                            .build();
                    
                    logger.info("Canal gRPC creado exitosamente");
                    logger.info("Estado inicial del canal: " + channel.getState(false));
                }
            }
        }
        return channel;
    }
    
    /**
     * Método de prueba estático
     */
    public static String testMethod() {
        return "StaticRecursoConfiableClient loaded successfully at " + System.currentTimeMillis();
    }
    
    /**
     * Registra una placa en el servicio gRPC (versión con Map)
     */
    public static String registerPlate(Map<String, Object> plateData) {
        logger.info("=== INICIO registerPlate (Map) ===");
        
        if (plateData == null) {
            return "ERROR: plateData cannot be null";
        }
        
        String plate = (String) plateData.get("plate");
        String loadId = (String) plateData.get("loadId");
        String loadNumber = (String) plateData.get("loadNumber");
        
        return registerPlate(plate, loadId, loadNumber);
    }
    
    /**
     * MÉTODO PRINCIPAL - Registra una placa en el servicio gRPC
     * 
     * Este es el método más importante que debes usar en tu nueva API.
     * Realiza una llamada gRPC real al webhook configurado.
     * 
     * @param plate Número de placa del vehículo (ej: "ABC123")
     * @param loadId Identificador único de la carga (ej: "LOAD001") 
     * @param loadNumber Número de la carga (ej: "LN001")
     * @return String con la respuesta del webhook en formato JSON estructurado
     * 
     * FORMATO DE RESPUESTA:
     * - Éxito: "WEBHOOK_RESPONSE: {json con datos del webhook}"
     * - Error gRPC: "STATIC_gRPC_ERROR: [detalles del error]"
     * - Error general: "STATIC_ERROR: [mensaje de error]"
     * 
     * EJEMPLO DE USO:
     * String response = StaticRecursoConfiableClient.registerPlate("ABC123", "LOAD001", "LN001");
     */
    public static String registerPlate(String plate, String loadId, String loadNumber) {
        logger.info("=== INICIO registerPlate (parámetros) ===");
        logger.info("Registrando placa: " + plate + ", LoadId: " + loadId + ", LoadNumber: " + loadNumber);
        
        try {
            // 1. OBTENER CANAL gRPC - Reutiliza conexión existente o crea nueva
            ManagedChannel currentChannel = getChannel();
            logger.info("Estado del canal gRPC: " + currentChannel.getState(false));
            
            // 2. VERIFICAR ESTADO DEL CANAL - Asegurar que esté disponible
            if (currentChannel.isShutdown() || currentChannel.isTerminated()) {
                throw new RuntimeException("Canal gRPC está cerrado o terminado");
            }
            
            // 3. CREAR STUB gRPC - Interfaz para comunicarse con el webhook
            // IMPORTANTE: Usa las clases generadas por protobuf (RecursoConfiableRPCGrpc)
            RecursoConfiableRPCGrpc.RecursoConfiableRPCBlockingStub blockingStub = 
                RecursoConfiableRPCGrpc.newBlockingStub(currentChannel);
            
            // 4. CONSTRUIR REQUEST - Mensaje gRPC con los datos de entrada
            // IMPORTANTE: Usa las clases generadas por protobuf (RecursoConfiable.RegisterRequest)
            RecursoConfiable.RegisterRequest request = RecursoConfiable.RegisterRequest.newBuilder()
                    .setPlate(plate)        // Placa del vehículo
                    .setLoadId(loadId)      // ID de la carga
                    .setLoadNumber(loadNumber) // Número de carga
                    .build();
            
            logger.info("Enviando request gRPC al webhook: " + request.toString());
            
            // 5. LLAMADA gRPC REAL - Comunicación con el webhook remoto
            // CRÍTICO: Esta es la llamada real al webhook.traxporta.com:443
            RecursoConfiable.RegisterReply reply = blockingStub.registerPlate(request);
            
            // 6. PROCESAR RESPUESTA - Extraer datos del webhook
            String webhookResponse = reply.getMessage();
            logger.info("Respuesta recibida del webhook: " + webhookResponse);
            
            // 7. FORMATEAR RESPUESTA - Estructura JSON para fácil parsing
            // FORMATO: Prefijo "WEBHOOK_RESPONSE:" + JSON con datos estructurados
            String response = "WEBHOOK_RESPONSE: {" +
                "\"status\": \"SUCCESS\", " +
                "\"webhook_raw_response\": \"" + webhookResponse + "\", " +
                "\"plate_registered\": \"" + plate + "\", " +
                "\"load_id\": \"" + loadId + "\", " +
                "\"load_number\": \"" + loadNumber + "\", " +
                "\"server\": \"webhook.traxporta.com:443\", " +
                "\"timestamp\": \"" + java.time.LocalDateTime.now().toString() + "\", " +
                "\"connection_status\": \"CONNECTED\"" +
                "}";
            
            logger.info("Respuesta del webhook real: " + webhookResponse);
            logger.info("Respuesta estructurada completa: " + response);
            logger.info("=== FIN registerPlate - RETORNANDO: " + response + " ===");
            
            return response;
            
        } catch (StatusRuntimeException e) {
            // MANEJO DE ERRORES gRPC - Problemas de comunicación con el webhook
            // Estos errores indican problemas específicos del protocolo gRPC
            String errorMessage = "STATIC_gRPC_ERROR: Status=" + e.getStatus() + 
                                ", Descripción=" + e.getStatus().getDescription() + 
                                ", Causa=" + e.getCause();
            logger.log(Level.SEVERE, "Error gRPC en registerPlate: " + errorMessage, e);
            logger.info("=== FIN registerPlate - ERROR gRPC ===");
            return errorMessage;
            
        } catch (Exception e) {
            // MANEJO DE ERRORES GENERALES - Cualquier otro tipo de error
            // Incluye problemas de red, configuración, etc.
            String errorMessage = "STATIC_ERROR: " + e.getMessage();
            logger.log(Level.SEVERE, "Error general en registerPlate: " + errorMessage, e);
            logger.info("=== FIN registerPlate - ERROR GENERAL ===");
            return errorMessage;
        }
    }

    /**
     * MÉTODO ALTERNATIVO - Registra placa usando Map<String, String>
     * 
     * Versión alternativa que acepta Map con valores String específicamente.
     * Útil cuando se garantiza que todos los valores son String.
     * 
     * @param plateData Map con los datos de la placa (plate, loadId, loadNumber)
     * @return String con la respuesta del webhook o mensaje de error
     * 
     * Ejemplo de uso:
     * Map<String, String> data = new HashMap<>();
     * data.put("plate", "ABC123");
     * data.put("loadId", "LOAD001"); 
     * data.put("loadNumber", "LN001");
     * String response = StaticRecursoConfiableClient.registerPlateFromStringMap(data);
     */
    public static String registerPlateFromStringMap(Map<String, String> plateData) {
        logger.info("=== INICIO registerPlateFromStringMap (Map<String,String>) ===");
        
        // EXTRAER DATOS DEL MAP - Obtener valores requeridos
        String plate = plateData.get("plate");
        String loadId = plateData.get("loadId");
        String loadNumber = plateData.get("loadNumber");
        
        logger.info("Datos extraídos del Map - Placa: " + plate + ", LoadId: " + loadId + ", LoadNumber: " + loadNumber);
        
        // DELEGAR AL MÉTODO PRINCIPAL - Reutilizar lógica existente
        return registerPlate(plate, loadId, loadNumber);
    }
    
    /**
     * Registra una placa simple (solo con placa)
     */
    public static String registerPlate(String plate) {
        return registerPlate(plate, "DEFAULT_LOAD_ID", "DEFAULT_LOAD_NUMBER");
    }
    
    /**
     * MÉTODO DE INFORMACIÓN - Obtiene detalles del canal gRPC
     * 
     * Útil para debugging y monitoreo de la conexión.
     * 
     * @return String con información del estado del canal
     */
    public static String getChannelInfo() {
        logger.info("=== INICIO getChannelInfo ===");
        try {
            ManagedChannel currentChannel = getChannel();
            String info = "Canal gRPC - Estado: " + currentChannel.getState(false) + 
                         ", Autoridad: " + currentChannel.authority() + 
                         ", Cerrado: " + currentChannel.isShutdown() + 
                         ", Terminado: " + currentChannel.isTerminated();
            logger.info("Información del canal: " + info);
            logger.info("=== FIN getChannelInfo ===");
            return info;
        } catch (Exception e) {
            String error = "Error obteniendo información del canal: " + e.getMessage();
            logger.log(Level.SEVERE, error, e);
            return error;
        }
    }
    
    /**
     * MÉTODO DE ESTADO - Verifica el estado del cliente gRPC
     * 
     * Útil para verificar si la conexión está disponible antes de hacer llamadas.
     * 
     * @return String con el estado actual del cliente
     */
    public static String getClientStatus() {
        logger.info("=== INICIO getClientStatus ===");
        try {
            ManagedChannel currentChannel = getChannel();
            
            // VERIFICAR MÚLTIPLES ESTADOS - Información completa del canal
            boolean isShutdown = currentChannel.isShutdown();
            boolean isTerminated = currentChannel.isTerminated();
            String state = currentChannel.getState(false).toString();
            
            String status = "STATIC_CLIENT_STATUS: {" +
                "\"ready\": " + (!isShutdown && !isTerminated) + ", " +
                "\"state\": \"" + state + "\", " +
                "\"shutdown\": " + isShutdown + ", " +
                "\"terminated\": " + isTerminated + ", " +
                "\"server\": \"" + HOST + ":" + PORT + "\"" +
                "}";
            
            logger.info("Estado del cliente: " + status);
            logger.info("=== FIN getClientStatus ===");
            return status;
            
        } catch (Exception e) {
            String error = "STATIC_CLIENT_ERROR: " + e.getMessage();
            logger.log(Level.SEVERE, "Error obteniendo estado del cliente: " + error, e);
            return error;
        }
    }
    
    /**
     * MÉTODO DE VERIFICACIÓN - Verifica si el canal está cerrado
     * 
     * @return boolean true si el canal está cerrado, false si está disponible
     */
    public static boolean isShutdown() {
        try {
            return channel != null && channel.isShutdown();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verificando estado shutdown: " + e.getMessage(), e);
            return true; // Asumir cerrado en caso de error
        }
    }
    
    /**
     * MÉTODO DE LIMPIEZA - Cierra el canal gRPC
     * 
     * IMPORTANTE: Llama este método cuando termines de usar el cliente
     * para liberar recursos y cerrar conexiones apropiadamente.
     * 
     * @return String confirmación del cierre
     */
    public static String shutdown() {
        logger.info("=== INICIO shutdown ===");
        synchronized (lock) {
            if (channel != null && !channel.isShutdown()) {
                try {
                    // CIERRE ORDENADO - Permite completar operaciones en curso
                    channel.shutdown();
                    
                    // ESPERAR TERMINACIÓN - Máximo 5 segundos
                    if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        // FORZAR CIERRE - Si no se cierra en 5 segundos
                        channel.shutdownNow();
                        logger.log(Level.WARNING, "Canal gRPC forzado a cerrar después de timeout");
                    }
                    
                    String result = "STATIC_SHUTDOWN: Canal gRPC cerrado correctamente";
                    logger.info(result);
                    logger.info("=== FIN shutdown ===");
                    return result;
                    
                } catch (InterruptedException e) {
                    String error = "STATIC_SHUTDOWN_ERROR: Interrupción durante el cierre";
                    logger.log(Level.WARNING, error, e);
                    Thread.currentThread().interrupt();
                    return error;
                }
            } else {
                String result = "STATIC_SHUTDOWN: Canal ya estaba cerrado";
                logger.info(result);
                return result;
            }
        }
    }
    
    /**
     * MÉTODO DE DIAGNÓSTICO - Información detallada del cliente gRPC
     * 
     * Proporciona información completa para debugging y monitoreo.
     * Incluye configuración, estado y estadísticas de conexión.
     * 
     * @return Map<String, Object> con información detallada del cliente
     */
    public static java.util.Map<String, Object> getDetailedInfo() {
        logger.info("=== INICIO getDetailedInfo ===");
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        
        try {
            // INFORMACIÓN BÁSICA - Configuración del cliente
            info.put("server_host", HOST);
            info.put("server_port", PORT);
            info.put("client_class", "StaticRecursoConfiableClient");
            info.put("timestamp", java.time.LocalDateTime.now().toString());
            
            // ESTADO DEL CANAL - Si existe
            if (channel != null) {
                ManagedChannel currentChannel = getChannel();
                info.put("channel_state", currentChannel.getState(false).toString());
                info.put("channel_authority", currentChannel.authority());
                info.put("is_shutdown", currentChannel.isShutdown());
                info.put("is_terminated", currentChannel.isTerminated());
                info.put("channel_exists", true);
            } else {
                info.put("channel_exists", false);
                info.put("channel_state", "NOT_CREATED");
            }
            
            // CONFIGURACIÓN TLS - Información de seguridad
            info.put("tls_enabled", true);
            info.put("keep_alive_time", "30 seconds");
            info.put("keep_alive_timeout", "5 seconds");
            info.put("max_message_size", "4MB");
            
            logger.info("Información detallada generada: " + info.toString());
            logger.info("=== FIN getDetailedInfo ===");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generando información detallada: " + e.getMessage(), e);
            info.put("error", "Error obteniendo información: " + e.getMessage());
        }
        
        return info;
    }
}