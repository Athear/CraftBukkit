package org.bukkit.craftbukkit.entity;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.server.World;
import net.minecraft.server.Entity;

import org.bukkit.craftbukkit.CraftServer;

import org.junit.Before;
import org.junit.Test;

public class EntityTranslationTest{
    /**
     * @server is used for CraftBukkit entity instantiation.
     * @world is used for Minecraft entitty instantiation.
     * Both are dummy classes, and any actual interaction should not be attempted.
     * 
     * @entityCraftTranslations is a map from Minecraft Entities to Craftbukkit CraftEntities.
     */
    static CraftServer server = null;
    World world = null;
    static Set<String> entityNames;
    HashMap<String,String> entityCraftTranslations;
    
    /**
     * Certain entities have been skipped in testing, due to difficulty of setup.
     * These include: 
     *      EntityPlayer, EntityLightening, EntityComplexPart (complex constructors) 
     *      EntityMinecartCommandBlock, EntityEnderman, EntityHorse (potentially require more world information. Superclasses unavailable to check)
     * Abstract classes have been skipped as well. These are:
     *      EntityLiving, EntityHuman, EntityCreature, EntityAnimal, EntityMonster, EntityWaterAnimal, EntityFlying, EntityAmbient, EntityFireball, EntityWeather, EntityHanging
     */
    @Before
    public void setup(){
        entityCraftTranslations = new HashMap<String,String>();
        
        entityCraftTranslations.put("EntityChicken","CraftChicken");
        entityCraftTranslations.put("EntityCow","CraftCow");
        entityCraftTranslations.put("EntityMushroomCow","CraftMushroomCow");
        entityCraftTranslations.put("EntityPig","CraftPig");
        entityCraftTranslations.put("EntitySheep","CraftSheep");
        entityCraftTranslations.put("EntityWolf","CraftWolf");
        entityCraftTranslations.put("EntityOcelot","CraftOcelot");
        entityCraftTranslations.put("EntityZombie","CraftZombie");
        entityCraftTranslations.put("EntityPigZombie","CraftPigZombie");
        entityCraftTranslations.put("EntityCreeper","CraftCreeper");
        entityCraftTranslations.put("EntitySilverfish","CraftSilverfish");
        entityCraftTranslations.put("EntityGiantZombie","CraftGiant");
        entityCraftTranslations.put("EntitySkeleton","CraftSkeleton");
        entityCraftTranslations.put("EntityBlaze","CraftBlaze");
        entityCraftTranslations.put("EntityWitch","CraftWitch");
        entityCraftTranslations.put("EntityWither","CraftWither");
        entityCraftTranslations.put("EntitySpider","CraftSpider");
        entityCraftTranslations.put("EntityCaveSpider","CraftCaveSpider");
        entityCraftTranslations.put("EntitySquid","CraftSquid");
        entityCraftTranslations.put("EntitySnowman","CraftSnowman");
        entityCraftTranslations.put("EntityIronGolem","CraftIronGolem");
        entityCraftTranslations.put("EntityVillager","CraftVillager");
        entityCraftTranslations.put("EntitySlime","CraftSlime");
        entityCraftTranslations.put("EntityMagmaCube","CraftMagmaCube");
        entityCraftTranslations.put("EntityGhast","CraftGhast");
        entityCraftTranslations.put("EntityEnderDragon","CraftEnderDragon");
        entityCraftTranslations.put("EntityBat","CraftBat");
        entityCraftTranslations.put("EntityExperienceOrb","CraftExperienceOrb");
        entityCraftTranslations.put("EntityArrow","CraftArrow");
        entityCraftTranslations.put("EntityBoat","CraftBoat");
        entityCraftTranslations.put("EntityEgg","CraftEgg");
        entityCraftTranslations.put("EntitySnowball","CraftSnowball");
        entityCraftTranslations.put("EntityPotion", "CraftThrownPotion");
        entityCraftTranslations.put("EntityEnderPearl","CraftEnderPearl");
        entityCraftTranslations.put("EntityThrownExpBottle","CraftThrownExpBottle");
        entityCraftTranslations.put("EntityFallingBlock","CraftFallingSand");
        entityCraftTranslations.put("EntitySmallFireball","CraftSmallFireball");
        entityCraftTranslations.put("EntityLargeFireball","CraftLargeFireball");
        entityCraftTranslations.put("EntityWitherSkull","CraftWitherSkull");
        entityCraftTranslations.put("EntityEnderSignal","CraftEnderSignal");
        entityCraftTranslations.put("EntityEnderCrystal","CraftEnderCrystal");
        entityCraftTranslations.put("EntityFishingHook", "CraftFish");
        entityCraftTranslations.put("EntityItem","CraftItem");
        entityCraftTranslations.put("EntityMinecartFurnace","CraftMinecartFurnace");
        entityCraftTranslations.put("EntityMinecartChest","CraftMinecartChest");
        entityCraftTranslations.put("EntityMinecartTNT","CraftMinecartTNT");
        entityCraftTranslations.put("EntityMinecartHopper","CraftMinecartHopper");
        entityCraftTranslations.put("EntityMinecartMobSpawner","CraftMinecartMobSpawner");
        entityCraftTranslations.put("EntityMinecartRideable","CraftMinecartRideable");
        entityCraftTranslations.put("EntityPainting","CraftPainting");
        entityCraftTranslations.put("EntityItemFrame","CraftItemFrame");
        entityCraftTranslations.put("EntityLeash","CraftLeash");
        entityCraftTranslations.put("EntityTNTPrimed","CraftTNTPrimed");
        entityCraftTranslations.put("EntityFireworks", "CraftFirework");

        entityNames = entityCraftTranslations.keySet();
    }

    

    private Entity generateEntity(String entityClassName){
        Entity entity = null;
        try { 
            Class entityClass = Class.forName("net.minecraft.server."+entityClassName);
            if(!Modifier.isAbstract(entityClass.getModifiers())){
              Constructor entityConstructor = entityClass.getConstructor(World.class);
              entity = (Entity) entityConstructor.newInstance(world);
            }else{
                throw new InstantiationException();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("There is no class by that name.");
            e.printStackTrace();
        } catch (SecurityException e) {
            System.err.print("Access to class constrcutor denied.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Too few / too many arguments passed to constructor");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Access to class constructor denied.");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("Entity constructor requires more input than a world");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Entity may require world information when initializing. World is presently NULL");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Entity class is an abstract class, and cannot be instantiated.");
            e.printStackTrace();
        }
        
        return entity;
    }
    
    
    @Test
    public void testEntityGenerationWorks(){
        Entity entity = null;
        for(String entityName:entityNames){
            entity = generateEntity(entityName);
            assertNotNull("Entity "+entityName+" was not be intantiated.",entity);
        }
        
    }
    
    @Test
    public void testGetEntityCompletes() {
        Entity entity = null;
        CraftEntity entityTransformed = null;
        for(String entityName:entityNames){
            entity = generateEntity(entityName);
            entityTransformed = CraftEntity.getEntity(server, entity);
            assertNotNull("Entity "+entityName+" could not be translated into CraftEntity.",entityTransformed);
        }
    }
    
    @Test
    public void testGetEntityReturnsCorrectEntity(){
        Entity entity = null;
        CraftEntity entityShell;
        for(String entityName:entityNames){
            entity = generateEntity(entityName);
            entityShell = CraftEntity.getEntity(server, entity);
            assertEquals(entityCraftTranslations.get(entityName), entityShell.getClass().getSimpleName());
        }
    }

}