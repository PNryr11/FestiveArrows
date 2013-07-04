package me.yukonapplegeek.festivearrows;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class FestiveArrows extends JavaPlugin implements Listener {

    private static void attachFirework(Arrow arrow) {
        FireworkMeta fireworkMeta = (FireworkMeta) (new ItemStack(Material.FIREWORK)).getItemMeta();
        Location location = arrow.getLocation();
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(Type.BURST)
                .withColor(Color.RED).withColor(Color.WHITE).withColor(Color.BLUE)
                .withTrail()
                .build());
        firework.setFireworkMeta(fireworkMeta);
        arrow.setPassenger(firework);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((JavaPlugin) this);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        Projectile projectile = (Projectile) event.getProjectile();
        if (projectile instanceof Arrow) {
            FestiveArrows.attachFirework((Arrow) projectile);
            projectile.setMetadata("festivearrows.hasLanded", new FixedMetadataValue(this, new Boolean(false)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(final ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            Boolean hasLanded = (Boolean) projectile.getMetadata("festivearrows.hasLanded").get(0).value();
            if (hasLanded != null && !hasLanded) {
                Location location = projectile.getLocation();
                location.getWorld().createExplosion(location, 4F, false);
                projectile.removeMetadata("festivearrows.hasLanded", this);
                projectile.setMetadata("festivearrows.hasLanded", new FixedMetadataValue(this, new Boolean(true)));
            }
        }
    }

    @EventHandler
    public void onDispenseEntity(BlockDispenseEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Arrow) {
            FestiveArrows.attachFirework((Arrow) entity);
            entity.setMetadata("festivearrows.hasLanded", new FixedMetadataValue(this, new Boolean(false)));
        }
    }

}