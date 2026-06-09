package com.cony.stockmanager.scheduler;

import com.cony.stockmanager.entity.StockMovement;
import com.cony.stockmanager.repository.ProductRepository;
import com.cony.stockmanager.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class DailyReportScheduler {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0 0 * * *") // cada día a medianoche
    public void generateDailyReport() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        List<StockMovement> movements = stockMovementRepository
                .findAllByCreatedAtBetween(startOfDay, endOfDay);

        long totalIn = movements.stream()
                .filter(m -> m.getType() == StockMovement.MovementType.IN)
                .mapToLong(m -> m.getQuantity())
                .sum();

        long totalOut = movements.stream()
                .filter(m -> m.getType() == StockMovement.MovementType.OUT)
                .mapToLong(m -> m.getQuantity())
                .sum();

        long totalAdjustments = movements.stream()
                .filter(m -> m.getType() == StockMovement.MovementType.ADJUSTMENT)
                .count();

        List<String> lowStockProducts = productRepository
                .findAllByActiveTrueAndStockLessThanEqual(5)
                .stream()
                .map(p -> p.getName() + " (stock: " + p.getStock() + ")")
                .collect(Collectors.toList());

        log.info("====== REPORTE DIARIO DE STOCK ======");
        log.info("Fecha: {}", startOfDay.toLocalDate());
        log.info("Total movimientos: {}", movements.size());
        log.info("Entradas (IN): {} unidades", totalIn);
        log.info("Salidas (OUT): {} unidades", totalOut);
        log.info("Ajustes: {}", totalAdjustments);
        log.info("Productos con stock bajo: {}", lowStockProducts.isEmpty() ? "Ninguno" : lowStockProducts);
        log.info("=====================================");
    }

    // Para probar sin esperar medianoche — corre cada 2 minutos
    @Scheduled(fixedRate = 120000)
    public void checkLowStock() {
        List<String> lowStock = productRepository
                .findAllByActiveTrueAndStockLessThanEqual(5)
                .stream()
                .map(p -> p.getName() + " (stock: " + p.getStock() + ")")
                .collect(Collectors.toList());

        if (!lowStock.isEmpty()) {
            log.warn("⚠️  PRODUCTOS CON STOCK BAJO: {}", lowStock);
        }
    }
}