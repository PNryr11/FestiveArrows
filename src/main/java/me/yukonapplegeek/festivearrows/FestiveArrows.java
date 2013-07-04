package me.yukonapplegeek.festivearrows;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class FestiveArrows extends JavaPlugin implements Listener {

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
        FireworkMeta fireworkMeta = (FireworkMeta) (new ItemStack(Material.FIREWORK)).getItemMeta();
        Firework firework = (Firework) projectile.getLocation().getWorld().spawnEntity(projectile.getLocation(), EntityType.FIREWORK);

        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(Type.BURST)
                .withColor(Color.RED).withColor(Color.WHITE).withColor(Color.BLUE)
                .withTrail()
                .build());
        firework.setFireworkMeta(fireworkMeta);
        projectile.setPassenger(firework);

        projectile.setMetadata("festivearrows.hasLanded", new FixedMetadataValue(this, new Boolean(false)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(final ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            Boolean hasLanded = (Boolean) projectile.getMetadata("festivearrows.hasLanded").get(0).value();
            if (hasLanded != null && !hasLanded) {
                Location location = projectile.getLocation();
                location.getWorld().createExplosion(location, 4F, false);
                projectile.getMetadata("festivearrows.hasLanded").set(0, new FixedMetadataValue(this, new Boolean(true)));
            }
        }
    }

}