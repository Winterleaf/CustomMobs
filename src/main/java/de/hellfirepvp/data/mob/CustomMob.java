package de.hellfirepvp.data.mob;

import de.hellfirepvp.CustomMobs;
import de.hellfirepvp.data.nbt.NBTTagType;
import de.hellfirepvp.data.nbt.WrappedNBTTagCompound;
import de.hellfirepvp.data.nbt.WrappedNBTTagList;
import de.hellfirepvp.file.write.MobDataWriter;
import de.hellfirepvp.nms.NMSReflector;
import de.hellfirepvp.spawning.SpawnLimitException;
import de.hellfirepvp.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the CustomMobs Plugin
 * The plugin can be found at: https://www.spigotmc.org/resources/custommobs.7339
 * Class: CustomMob
 * Created by HellFirePvP
 * Date: 24.05.2016 / 16:27
 */
public class CustomMob {

    private static final Map<CustomMob, List<LivingEntity>> alive = new HashMap<>();

    private final String name;
    protected WrappedNBTTagCompound snapshotTag;
    private EntityAdapter entityAdapter;
    private DataAdapter dataAdapter;

    public CustomMob(String name, WrappedNBTTagCompound data) {
        this.name = name;
        this.snapshotTag = data;
        this.entityAdapter = new EntityAdapter(this);
        this.dataAdapter = new DataAdapter(this);
    }

    public EntityAdapter getEntityAdapter() {
        return entityAdapter;
    }

    public DataAdapter getDataAdapter() {
        return dataAdapter;
    }

    protected void updateTag() {
        this.snapshotTag = entityAdapter.getEntityTag();
        MobDataWriter.writeMobFile(this);
        entityAdapter.reloadEntity();
    }

    public String getMobFileName() {
        return name;
    }

    public WrappedNBTTagCompound getDataSnapshot() {
        return snapshotTag;
    }

    public LivingEntity spawnAt(Location l) throws SpawnLimitException {
        if(!CustomMobs.instance.getSpawnLimiter().canSpawn(this)) throw new SpawnLimitException("SpawnLimit denies spawning of " + name);
        LivingEntity entity = unsafe_Spawn(l);
        if(entity == null)
            throw new IllegalStateException("Unknown EntityType for " + getMobFileName() + " or NBTTag is missing some information.");
        CustomMobs.instance.getSpawnLimiter().spawnedIncrement(this, entity);
        return entity;
    }

    private LivingEntity unsafe_Spawn(Location l) {
        LivingEntity le = NMSReflector.mobTypeProvider.spawnEntityFromSerializedData(l, snapshotTag);
        if(le == null) return null;
        EntityEquipment ee = le.getEquipment();
        ee.setBootsDropChance(0F);
        ee.setLeggingsDropChance(0F);
        ee.setChestplateDropChance(0F);
        ee.setHelmetDropChance(0F);
        ee.setItemInMainHandDropChance(0F);
        ee.setItemInOffHandDropChance(0F);

        le.setRemoveWhenFarAway(false);
        le.setCanPickupItems(false);

        if(dataAdapter.isFireProof()) {
            EntityUtils.setFireproof(le);
        }
        NMSReflector.nmsUtils.setName(le, getMobFileName());
        return le;
    }

    //
    // Registry removal methods.
    //

    public static void kill(CustomMob mob, LivingEntity entity) {
        List<LivingEntity> entities = alive.get(mob);
        if(entities != null) {
            entities.remove(entity);
        }
        if(mob.dataAdapter.getSpawnLimit() > 0) {
            CustomMobs.instance.getSpawnLimiter().decrement(mob, entity);
        }
    }

    public static int killAll(String mobName) {
        CustomMob mob = CustomMobs.instance.getMobDataHolder().getCustomMob(mobName);
        if(mob != null) return killAll(mob);
        return 0;
    }

    public static int killAll(CustomMob mob) {
        int count = 0;
        List<LivingEntity> aliveInstances = alive.get(mob);
        if(aliveInstances != null) {
            for(LivingEntity entity : aliveInstances) {
                count++;
                entity.remove();
                if(mob.dataAdapter.getSpawnLimit() > 0) {
                    CustomMobs.instance.getSpawnLimiter().decrement(mob, entity);
                }
            }
            alive.get(mob).clear();
        }
        return count;
    }

    public static void killAll() {
        alive.keySet().forEach(de.hellfirepvp.data.mob.CustomMob::killAll);
        alive.clear();
    }

    public static int killAllWithLimit() {
        int count = 0;
        for(CustomMob mob : alive.keySet()) {
            if(mob.dataAdapter.getSpawnLimit() > 0) {
                count += killAll(mob);
            }
        }
        return count;
    }

}