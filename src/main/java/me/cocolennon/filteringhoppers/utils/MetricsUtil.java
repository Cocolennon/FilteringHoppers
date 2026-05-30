package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;

import java.util.concurrent.atomic.AtomicInteger;

public class MetricsUtil {
    public final static AtomicInteger allowedItems = new AtomicInteger(0);
    public final static AtomicInteger deniedItems = new AtomicInteger(0);
    public final static AtomicInteger destroyedItems = new AtomicInteger(0);

    public static void register(Main instance) {
        Metrics metrics = new Metrics(instance, 31493);
        metrics.addCustomChart(new SingleLineChart("allowedItems", () -> allowedItems.getAndSet(0)));
        metrics.addCustomChart(new SingleLineChart("deniedItems", () -> deniedItems.getAndSet(0)));
        metrics.addCustomChart(new SingleLineChart("destroyedItems", () -> destroyedItems.getAndSet(0)));
    }
}
