package com.traxion.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Cliente gRPC simplificado para el servicio RecursoConfiable
 * Se conecta a webhook.traxporta.com:443
 * Implementa patrón Singleton para reutilizar el canal gRPC
 */
public class RecursoConfiableClient {
    
    private static final Logger logger = Logger.getLogger(RecursoConfiableClient.class.getName());
    
    // Instancia singleton
    private static volatile RecursoConfiableClient instance;
    private static final Object lock = new Object();
    
    private final ManagedChannel channel;
    private final String host;
    private final int port;
    
    /**
     * Constructor privado del cliente gRPC (Singleton)
     */
    private RecursoConfiableClient() {
        this("webhook.traxporta.com", 443);
    }
    
    /**
     * Constructor privado con host y puerto personalizados
     */
    private RecursoConfiableClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .useTransportSecurity() // Habilita TLS para puerto 443
                .keepAliveTime(30, TimeUnit.SECONDS) // Keep alive para mantener conexión
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(4 * 1024 * 1024) // 4MB max message size
                .build();
        
        logger.info("Cliente gRPC inicializado para " + host + ":" + port);
    }
    
    /**
     * Obtiene la instancia singleton del cliente gRPC
     */
    public static RecursoConfiableClient getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new RecursoConfiableClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * Método estático para crear cliente con configuración por defecto (mantiene compatibilidad)
     */
    public static RecursoConfiableClient createDefault() {
        return getInstance();
    }
    
    /**
     * Registra una placa en el servicio gRPC
     * Implementación que realiza llamada real al servicio gRPC
     */
    public String registerPlate(String plate, String loadId, String loadNumber) {
        logger.info("=== INICIO registerPlate ===");
        logger.info("Registrando placa: " + plate + ", LoadId: " + loadId + ", LoadNumber: " + loadNumber);
        logger.info("Estado del canal gRPC: " + channel.getState(false));
        
        try {
            // Crear el stub gRPC usando las clases generadas
            RecursoConfiableRPCGrpc.RecursoConfiableRPCBlockingStub blockingStub = 
                RecursoConfiableRPCGrpc.newBlockingStub(channel);
            
            // Crear el request con los datos proporcionados
            RecursoConfiable.RegisterRequest request = RecursoConfiable.RegisterRequest.newBuilder()
                    .setPlate(plate)
                    .setLoadId(loadId)
                    .setLoadNumber(loadNumber)
                    .build();
            
            logger.info("Enviando request gRPC al webhook: " + request.toString());
            
            // Realizar la llamada gRPC real al webhook
            RecursoConfiable.RegisterReply reply = blockingStub.registerPlate(request);
            
            // Obtener la respuesta del webhook
            String webhookResponse = reply.getMessage();
            logger.info("Respuesta recibida del webhook: " + webhookResponse);
            
            // Construir respuesta estructurada con la respuesta real del webhook
            String response = "gRPC_SUCCESS: " + webhookResponse + " - Placa " + plate + " registrada en webhook.traxporta.com:443 - LoadId: " + loadId;
            logger.info("Respuesta procesada del servicio gRPC: " + response);
            logger.info("=== FIN registerPlate - RETORNANDO: " + response + " ===");
            
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.log(Level.SEVERE, "Error gRPC: " + e.getStatus() + " - " + e.getMessage(), e);
            String errorResponse = "gRPC_ERROR: " + e.getStatus() + " - " + e.getMessage();
            logger.info("=== FIN registerPlate - RETORNANDO ERROR: " + errorResponse + " ===");
            return errorResponse;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al enviar placa al servicio gRPC: " + e.getMessage(), e);
            String errorResponse = "gRPC_EXCEPTION: " + e.getMessage();
            logger.info("=== FIN registerPlate - RETORNANDO EXCEPTION: " + errorResponse + " ===");
            return errorResponse;
        }
    }
    
    /**
     * Registra una placa usando un Map (para compatibilidad con Mule)
     */
    public String registerPlate(Map<String, Object> plateData) {
        String plate = (String) plateData.get("plate");
        String loadId = (String) plateData.get("loadId");
        String loadNumber = (String) plateData.get("loadNumber");
        
        return registerPlate(plate, loadId, loadNumber);
    }
    
    /**
     * Cierra la conexión del canal gRPC de forma segura
     */
    public static void shutdownGlobal() {
        synchronized (lock) {
            if (instance != null && !instance.channel.isShutdown()) {
                try {
                    logger.info("Cerrando canal gRPC global...");
                    instance.channel.shutdown();
                    if (!instance.channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        logger.warning("Canal gRPC no se cerró en 5 segundos, forzando cierre...");
                        instance.channel.shutdownNow();
                        if (!instance.channel.awaitTermination(5, TimeUnit.SECONDS)) {
                            logger.severe("Canal gRPC no pudo cerrarse completamente");
                        }
                    }
                    logger.info("Canal gRPC cerrado exitosamente");
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Interrupción durante el cierre del canal gRPC", e);
                    instance.channel.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    instance = null;
                }
            }
        }
    }
    
    /**
     * Método de shutdown para compatibilidad (ahora es no-op ya que el canal se reutiliza)
     */
    public void shutdown() throws InterruptedException {
        logger.info("Método shutdown() llamado - El canal se mantiene activo para reutilización");
        // No cerramos el canal aquí ya que se reutiliza globalmente
        // Para cerrar completamente, usar shutdownGlobal()
    }
    
    /**
     * Verifica si el canal está cerrado
     */
    public boolean isShutdown() {
        return channel.isShutdown();
    }
    
    /**
     * Obtiene información del canal
     */
    public String getChannelInfo() {
        String info = "gRPC Channel - Host: " + host + ", Port: " + port + ", State: " + channel.getState(false);
        logger.info("=== getChannelInfo RETORNANDO: " + info + " ===");
        return info;
    }
}