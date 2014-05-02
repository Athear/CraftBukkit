package org.bukkit.craftbukkit.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.*;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public abstract class CraftEntity implements org.bukkit.entity.Entity {
    protected final CraftServer server;
    protected Entity entity;
    private EntityDamageEvent lastDamageEvent;
    private static final HashMap<String,String> specialCraftNames = new HashMap<String,String>();
    static{
      //specially named CraftBukkit classes
        //For abstract Minecraft classes:
        specialCraftNames.put("EntityHuman", "CraftHumanEntity");
        specialCraftNames.put("EntityWaterAnimal", "CraftWaterMob");
        specialCraftNames.put("EntityLiving","CraftLivingEntity");
        specialCraftNames.put("EntityAnimal", "CraftAnimals");
        //For other Minecraft classes:
        specialCraftNames.put("EntityGiantZombie","CraftGiant");
        specialCraftNames.put("EntityPotion", "CraftThrownPotion");
        specialCraftNames.put("EntityFallingBlock","CraftFallingSand");
        specialCraftNames.put("EntityFishingHook", "CraftFish");
        specialCraftNames.put("EntityLightning", "CraftLightningStrike");
        specialCraftNames.put("EntityMinecartCommandBlock","CraftMinecartCommand");
        specialCraftNames.put("EntityFireworks", "CraftFirework");
    }

    public CraftEntity(final CraftServer server, final Entity entity) {
        this.server = server;
        this.entity = entity;
    }

    
    @SuppressWarnings("unchecked")
    public static CraftEntity getEntity(CraftServer server, Entity entity){
        String errorMessage ="Unknown Error in CraftEntity instantiation.";
        CraftEntity newEntity = null;
        
        Class<? extends Entity> entityClass = entity.getClass();
        String entityName = entityClass.getSimpleName();
        
        Class<CraftEntity> craftClass;
        String craftName;
        
        
        if(specialCraftNames.containsKey(entityName)){
            craftName = specialCraftNames.get(entityName);
        }else{
            craftName = "Craft"+entityName.substring(6);
        }
        try {
            craftClass = (Class<CraftEntity>) Class.forName("org.bukkit.craftbukkit.entity."+craftName);
            Constructor<CraftEntity> craftConstruct;
            try{
                craftConstruct = craftClass.getDeclaredConstructor(CraftServer.class, entityClass);
            }catch(NoSuchMethodException e){
                craftConstruct = craftClass.getDeclaredConstructor(CraftServer.class, entityClass.getSuperclass());
            }
            newEntity = craftConstruct.newInstance(server, entity);
        } catch (ClassNotFoundException e) {
            errorMessage = "Unknown entity " + entity.getClass().getName();
        } catch (SecurityException e) {
            errorMessage = "Access to class constructor denied";
        } catch (NoSuchMethodException e) {
            errorMessage =craftName+" contructor is missing or requires non-standard arguments. Expected inputs: Server, "+entityName+".";
        } catch (IllegalArgumentException e) {
            errorMessage = craftName+" contructor requires non-standard arguments. Expected inputs: Server, "+entityName+".";
        } catch (InstantiationException e) {
            errorMessage = craftName+" class is an abstract class, and cannot be instantiated.";
        } catch (IllegalAccessException e) {
            errorMessage = "Access to class constructor denied";
        } catch (InvocationTargetException e) {
            errorMessage = "Error thrown by "+craftName+" constructor: \n"+ e.getLocalizedMessage();
        }
        if(newEntity == null){
            throw new AssertionError(errorMessage);
        }
        return newEntity;
    }
    
    
    public Location getLocation() {
        return new Location(getWorld(), entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.locX);
            loc.setY(entity.locY);
            loc.setZ(entity.locZ);
            loc.setYaw(entity.yaw);
            loc.setPitch(entity.pitch);
        }

        return loc;
    }

    public Vector getVelocity() {
        return new Vector(entity.motX, entity.motY, entity.motZ);
    }

    public void setVelocity(Vector vel) {
        entity.motX = vel.getX();
        entity.motY = vel.getY();
        entity.motZ = vel.getZ();
        entity.velocityChanged = true;
    }

    public boolean isOnGround() {
        if (entity instanceof EntityArrow) {
            return ((EntityArrow) entity).isInGround();
        }
        return entity.onGround;
    }

    public World getWorld() {
        return entity.world.getWorld();
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        if (entity.vehicle != null || entity.passenger != null || entity.dead) {
            return false;
        }

        entity.world = ((CraftWorld) location.getWorld()).getHandle();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // entity.setLocation() throws no event, and so cannot be cancelled
        return true;
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        @SuppressWarnings("unchecked")
        List<Entity> notchEntityList = entity.world.getEntities(entity, entity.boundingBox.grow(x, y, z));
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    public int getEntityId() {
        return entity.getId();
    }

    public int getFireTicks() {
        return entity.fireTicks;
    }

    public int getMaxFireTicks() {
        return entity.maxFireTicks;
    }

    public void setFireTicks(int ticks) {
        entity.fireTicks = ticks;
    }

    public void remove() {
        entity.dead = true;
    }

    public boolean isDead() {
        return !entity.isAlive();
    }

    public boolean isValid() {
        return entity.isAlive() && entity.valid;
    }

    public Server getServer() {
        return server;
    }

    public Vector getMomentum() {
        return getVelocity();
    }

    public void setMomentum(Vector value) {
        setVelocity(value);
    }

    public org.bukkit.entity.Entity getPassenger() {
        return isEmpty() ? null : getHandle().passenger.getBukkitEntity();
    }

    public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        if (passenger instanceof CraftEntity) {
            ((CraftEntity) passenger).getHandle().setPassengerOf(getHandle());
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return getHandle().passenger == null;
    }

    public boolean eject() {
        if (getHandle().passenger == null) {
            return false;
        }

        getHandle().passenger.setPassengerOf(null);
        return true;
    }

    public float getFallDistance() {
        return getHandle().fallDistance;
    }

    public void setFallDistance(float distance) {
        getHandle().fallDistance = distance;
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageEvent = event;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageEvent;
    }

    public UUID getUniqueId() {
        return getHandle().uniqueID;
    }

    public int getTicksLived() {
        return getHandle().ticksLived;
    }

    public void setTicksLived(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        getHandle().ticksLived = value;
    }

    public Entity getHandle() {
        return entity;
    }

    public void playEffect(EntityEffect type) {
        this.getHandle().world.broadcastEntityEffect(getHandle(), type.getData());
    }

    public void setHandle(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "CraftEntity{" + "id=" + getEntityId() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftEntity other = (CraftEntity) obj;
        return (this.getEntityId() == other.getEntityId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.getEntityId();
        return hash;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public boolean isInsideVehicle() {
        return getHandle().vehicle != null;
    }

    public boolean leaveVehicle() {
        if (getHandle().vehicle == null) {
            return false;
        }

        getHandle().setPassengerOf(null);
        return true;
    }

    public org.bukkit.entity.Entity getVehicle() {
        if (getHandle().vehicle == null) {
            return null;
        }

        return getHandle().vehicle.getBukkitEntity();
    }
}
