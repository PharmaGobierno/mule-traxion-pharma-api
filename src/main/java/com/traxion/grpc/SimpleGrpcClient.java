package com.traxion.grpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Cliente gRPC simplificado para debugging
 * Esta versión no usa dependencias externas de gRPC para aislar el problema
 */
public class SimpleGrpcClient {
    
    private static final Logger logger = Logger.getLogger(SimpleGrpcClient.class.getName());
    
    private String status = "DISCONNECTED";
    private boolean isConnected = false;
    
    public SimpleGrpcClient() {
        logger.info("=== SimpleGrpcClient: Constructor llamado ===");
        initializeConnection();
    }
    
    /**
     * Crea una instancia por defecto del cliente
     */
    public static SimpleGrpcClient createDefault() {
        logger.info("=== SimpleGrpcClient: createDefault() llamado ===");
        return new SimpleGrpcClient();
    }
    
    /**
     * Inicializa la conexión simulada
     */
    private void initializeConnection() {
        try {
            logger.info("=== SimpleGrpcClient: Inicializando conexión simulada ===");
            
            // Simular inicialización
            Thread.sleep(100);
            
            this.isConnected = true;
            this.status = "CONNECTED";
            
            logger.info("=== SimpleGrpcClient: Conexión simulada establecida exitosamente ===");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "=== SimpleGrpcClient: Error en inicialización ===", e);
            this.isConnected = false;
            this.status = "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Registra una placa en el sistema gRPC simulado
     */
    public String registerPlate(String plate) {
        logger.info("=== SimpleGrpcClient: registerPlate() llamado con placa: " + plate + " ===");
        
        try {
            if (!isConnected) {
                logger.warning("=== SimpleGrpcClient: Cliente no conectado, intentando reconectar ===");
                initializeConnection();
            }
            
            if (isConnected) {
                // Simular procesamiento
                String response = "SIMULATED_RESPONSE_FOR_" + plate + "_" + System.currentTimeMillis();
                logger.info("=== SimpleGrpcClient: Placa registrada exitosamente. Respuesta: " + response + " ===");
                return response;
            } else {
                logger.warning("=== SimpleGrpcClient: No se pudo establecer conexión ===");
                return "ERROR_NO_CONNECTION";
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "=== SimpleGrpcClient: Error en registerPlate ===", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene información del canal
     */
    public String getChannelInfo() {
        logger.info("=== SimpleGrpcClient: getChannelInfo() llamado ===");
        
        try {
            if (isConnected) {
                String channelInfo = "SIMULATED_CHANNEL_INFO_" + System.currentTimeMillis();
                logger.info("=== SimpleGrpcClient: Channel info obtenida: " + channelInfo + " ===");
                return channelInfo;
            } else {
                logger.warning("=== SimpleGrpcClient: Canal no disponible - cliente desconectado ===");
                return "CHANNEL_UNAVAILABLE";
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "=== SimpleGrpcClient: Error en getChannelInfo ===", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene el estado del cliente
     */
    public String getClientStatus() {
        logger.info("=== SimpleGrpcClient: getClientStatus() llamado. Estado actual: " + status + " ===");
        return status;
    }
    
    /**
     * Cierra la conexión
     */
    public void shutdown() {
        logger.info("=== SimpleGrpcClient: shutdown() llamado ===");
        try {
            this.isConnected = false;
            this.status = "SHUTDOWN";
            logger.info("=== SimpleGrpcClient: Cliente cerrado exitosamente ===");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "=== SimpleGrpcClient: Error en shutdown ===", e);
        }
    }
    
    /**
     * Método de prueba para verificar que la clase se carga correctamente
     */
    public static String testMethod() {
        String result = "SimpleGrpcClient loaded successfully at " + System.currentTimeMillis();
        logger.info("=== SimpleGrpcClient: testMethod() llamado. Resultado: " + result + " ===");
        return result;
    }
}