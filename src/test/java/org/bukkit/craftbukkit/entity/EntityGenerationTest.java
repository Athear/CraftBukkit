package org.bukkit.craftbukkit.entity;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import net.minecraft.server.*;
import net.minecraft.server.Entity;


import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.support.AbstractTestingBase;
import org.bukkit.support.DummyServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EntityGenerationTest{
    /**
     * @server is used for CraftBukkit entity instantiation
     * @world is used for Minecraft entitty instantiation
     * Both are dummy classes, and full interaction should not be attempted.
     */
    static CraftServer server = null;
    World world =null;
    static String[] entityNames;
    static String[] craftNames;
    HashMap<String,String> specialCrafts;
    
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
        String[] names = {
                "EntityChicken","EntityCow","EntityMushroomCow","EntityPig","EntitySheep",
                "EntityWolf","EntityOcelot",
                "EntityZombie","EntityPigZombie","EntityCreeper","EntitySilverfish",
                "EntityGiantZombie","EntitySkeleton","EntityBlaze","EntityWitch","EntityWither","EntitySpider","EntityCaveSpider",
                "EntitySquid",
                "EntitySnowman","EntityIronGolem",
                "EntityVillager",
                "EntitySlime", "EntityMagmaCube",
                "EntityGhast",
                "EntityEnderDragon",
                "EntityBat",
                "EntityExperienceOrb","EntityArrow","EntityBoat",
                "EntityEgg","EntitySnowball","EntityPotion","EntityEnderPearl","EntityThrownExpBottle",
                "EntityFallingBlock",
                "EntitySmallFireball","EntityLargeFireball","EntityWitherSkull",
                "EntityEnderSignal","EntityEnderCrystal","EntityFishingHook","EntityItem",
                "EntityMinecartFurnace","EntityMinecartChest","EntityMinecartTNT","EntityMinecartHopper","EntityMinecartMobSpawner","EntityMinecartRideable",
                "EntityPainting","EntityItemFrame","EntityLeash",
                "EntityTNTPrimed","EntityFireworks"};
        String[] crafts = {
                "CraftChicken","CraftCow","CraftMushroomCow","CraftPig","CraftSheep",
                "CraftWolf","CraftOcelot",
                "CraftZombie","CraftPigZombie","CraftCreeper","CraftSilverfish",
                "CraftGiant","CraftSkeleton","CraftBlaze","CraftWitch","CraftWither","CraftSpider","CraftCaveSpider",
                "CraftSquid",
                "CraftSnowman","CraftIronGolem",
                "CraftVillager",
                "CraftSlime", "CraftMagmaCube",
                "CraftGhast",
                "CraftEnderDragon",
                "CraftBat",
                "CraftExperienceOrb","CraftArrow","CraftBoat",
                "CraftEgg","CraftSnowball","CraftThrownPotion","CraftEnderPearl","CraftThrownExpBottle",
                "CraftFallingSand",
                "CraftSmallFireball","CraftLargeFireball","CraftWitherSkull",
                "CraftEnderSignal","CraftEnderCrystal","CraftFish","CraftItem",
                "CraftMinecartFurnace","CraftMinecartChest","CraftMinecartTNT","CraftMinecartHopper","CraftMinecartMobSpawner","CraftMinecartRideable",
                "CraftPainting","CraftItemFrame","CraftLeash",
                "CraftTNTPrimed","CraftFirework"};
        
        specialCrafts = new HashMap<String,String>();
        specialCrafts.put("EntityGiantZombie","CraftGiant");
        specialCrafts.put("EntityPotion", "CraftThrownPotion");
        specialCrafts.put("EntityFallingBlock","CraftFallingSand");
        specialCrafts.put("EntityFishingHook", "CraftFish");
        specialCrafts.put("EntityFireworks", "EntityFirework");
        
        entityNames = names;
        craftNames = crafts;
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
            // TODO Auto-generated catch block "Too few / too many arguments passed to constructor"
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("Entity constructor requires more input than worlds");
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
    
    @Test //Test to ensure that both entityNames and craftNames are the same length
    public void testEnsureOneToOneNames(){
        assertEquals(craftNames.length, entityNames.length);
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
        for(int i = 0; i<entityNames.length; i++){
            entity = generateEntity(entityNames[i]);
            entityShell = CraftEntity.getEntity(server, entity);
            assertEquals(craftNames[i], entityShell.getClass().getSimpleName());
        }
    }

}
