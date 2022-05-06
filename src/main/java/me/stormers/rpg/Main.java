package me.stormers.rpg;

import me.stormers.rpg.Leveling.FarmMineListener;
import me.stormers.rpg.Leveling.KillEntityListener;
import me.stormers.rpg.StatWork.DamageListener;
import me.stormers.rpg.Listeners.JoinListener;
import me.stormers.rpg.Menu.MenuListener;
import me.stormers.rpg.StatWork.Regen;
import me.stormers.rpg.Stats.StatsClickListener;
import me.stormers.rpg.UniqueAbility.UniqueAbilityListener;
import me.stormers.rpg.UniqueAbility.UniqueAbilityWork;
import me.stormers.rpg.commands.Guilds.GuildCMDS;
import me.stormers.rpg.commands.Races.*;
import me.stormers.rpg.commands.ReloadCommand;
import me.stormers.rpg.Menu.menu;
import me.stormers.rpg.commands.SummonCmd;
import me.stormers.rpg.commands.Updates;
import me.stormers.rpg.commands.message;
import me.stormers.rpg.files.*;
import me.stormers.rpg.mobs.CustomMob;
import me.stormers.rpg.sql.MySql;
import me.stormers.rpg.sql.SQLGetter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

public final class Main extends JavaPlugin implements Listener {

    private static Logger logger;
    private ArrayList listP = new ArrayList();
    public MySql SQL;
    public SQLGetter data;
    private World world;


    private BukkitTask task;

    private final Map<Entity, Integer> indicators = new HashMap<>();

    private final Map<Entity, CustomMob> entities = new HashMap<>();

    private final DecimalFormat formatter = new DecimalFormat("#.##");
    @Override
    public void onEnable() {
        // Plugin startup logic

        this.SQL = new MySql();
        this.data = new SQLGetter(this);
        try {
            SQL.connect();
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("Database error");
        } catch (SQLException e) {
            Bukkit.getLogger().info("Database error");
        }

        if (SQL.connected()) {
            data.MakeTable();
            Bukkit.getLogger().info("Database connected");
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        List test1 = new ArrayList();
        test1.add("testC");

        List test2 = new ArrayList();
        test2.add("testS");

        List test3 = new ArrayList();
        test3.add("testG");
        List test4 = new ArrayList();
        test4.add("Player0");

        logger = getLogger();
        CClists.setup();
        CCStats.setup();
        CCMagic.setup();
        CCSkills.setup();
        CCPlayerInfo.setup();
        CCother.setup();
        CClists.get().options().copyDefaults(true);
        CCStats.get().options().copyDefaults(true);
        CCSkills.get().options().copyDefaults(true);
        CCMagic.get().options().copyDefaults(true);
        CCPlayerInfo.get().options().copyDefaults(true);
        CCother.get().options().copyDefaults(true);
        CClists.get().addDefault("Commander",test1);
        CClists.get().addDefault("Solo",test2);
        CClists.get().addDefault("Guilds",test3);
        CClists.get().addDefault("GuildJoined",test4);
        CClists.save();
        CCother.save();
        CCStats.save();
        CCMagic.save();
        CCSkills.save();
        CCPlayerInfo.save();

        getServer().getPluginManager().registerEvents(new JoinListener(this),this);
        getServer().getPluginManager().registerEvents(new MenuListener(),this);
        getServer().getPluginManager().registerEvents(new StatsClickListener(),this);
        getServer().getPluginManager().registerEvents(new DamageListener(),this);
        getServer().getPluginManager().registerEvents(new Regen(this),this);
        getServer().getPluginManager().registerEvents(new KillEntityListener(this),this);
        getServer().getPluginManager().registerEvents(new UniqueAbilityListener(),this);
        getServer().getPluginManager().registerEvents(new FarmMineListener(this),this);
        getServer().getPluginManager().registerEvents(new UniqueAbilityWork(),this);
        getServer().getPluginManager().registerEvents(new RacesInvListener(),this);

        getCommand("updates").setExecutor(new Updates());
        getCommand("preload").setExecutor(new ReloadCommand());
        getCommand("message").setExecutor(new message());
        getCommand("menu").setExecutor(new menu());
        getCommand("guild").setExecutor(new GuildCMDS());
        getCommand("races").setExecutor(new Races());
        getCommand("Seth").setExecutor(new SummonCmd());

        this.world = Bukkit.getWorld("world");
        getServer().getPluginManager().registerEvents(this, this);
        spawnMobs(9, 10, 100);
        (new BukkitRunnable() {
            final Set<Entity> stands = Main.this.indicators.keySet();

            final List<Entity> removal = new ArrayList<>();

            public void run() {
                for (Entity stand : this.stands) {
                    int ticksLeft = Main.this.indicators.get(stand).intValue();
                    if (ticksLeft == 0) {
                        stand.remove();
                        this.removal.add(stand);
                        continue;
                    }
                    ticksLeft--;
                    Main.this.indicators.put(stand, Integer.valueOf(ticksLeft));
                }
                this.stands.removeAll(this.removal);
            }
        }).runTaskTimer(this, 0L, 1L);
            }

    public void spawnMobs(final int size, final int mobCap, int spawnTime) {
        final CustomMob[] mobTypes = CustomMob.values();
        this

                .task = (new BukkitRunnable() {
            final Set<Entity> spawned = Main.this.entities.keySet();

            final List<Entity> removal = new ArrayList<>();

            public void run() {
                for (Entity entity : this.spawned) {
                    if (!entity.isValid() || entity.isDead())
                        this.removal.add(entity);
                }
                this.spawned.removeAll(this.removal);
                int diff = mobCap - Main.this.entities.size();
                if (diff <= 0)
                    return;
                int spawnAmount = (int)(Math.random() * (diff + 1)), count = 0;
                while (count <= spawnAmount) {
                    count++;
                    int ranX = Main.this.getRandomWithNeg(size), ranZ = Main.this.getRandomWithNeg(size);
                    Block block = Main.this.world.getHighestBlockAt(ranX, ranZ);
                    double xOffset = Main.this.getRandomOffset(), zOffset = Main.this.getRandomOffset();
                    Location loc = block.getLocation().clone().add(xOffset, 1.0D, zOffset);
                    if (!Main.this.isSpawnable(loc))
                        continue;
                    double random = Math.random() * 101.0D, previous = 0.0D;
                    CustomMob typeToSpawn = mobTypes[0];
                    for (CustomMob type : mobTypes) {
                        previous += type.getSpawnChance();
                        if (random <= previous) {
                            typeToSpawn = type;
                            break;
                        }
                    }
                    Main.this.entities.put(typeToSpawn.spawn(loc), typeToSpawn);
                }
            }
        }).runTaskTimer(this, 0L, spawnTime);
    }

    private boolean isSpawnable(Location loc) {
        Block feetBlock = loc.getBlock(), headBlock = loc.clone().add(0.0D, 1.0D, 0.0D).getBlock(), upperBlock = loc.clone().add(0.0D, 2.0D, 0.0D).getBlock();
        return (feetBlock.isPassable() && !feetBlock.isLiquid() && headBlock.isPassable() && !headBlock.isLiquid() && upperBlock.isPassable() && !upperBlock.isLiquid());
    }

    private double getRandomOffset() {
        double random = Math.random();
        if (Math.random() > 0.5D)
            random *= -1.0D;
        return random;
    }

    private int getRandomWithNeg(int size) {
        int random = (int)(Math.random() * (size + 1));
        if (Math.random() > 0.5D)
            random *= -1;
        return random;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity rawEntity = event.getEntity();
        if (!this.entities.containsKey(rawEntity))
            return;
        CustomMob mob = this.entities.get(rawEntity);
        LivingEntity entity = (LivingEntity)rawEntity;
        double damage = event.getFinalDamage(), health = entity.getHealth() + entity.getAbsorptionAmount();
        if (health > damage) {
            health -= damage;
            entity.setCustomName(Utils.color(mob.getName() + " &r&c" + (int)health + "/" + (int)mob.getMaxHealth() + ""));
        }
        Location loc = entity.getLocation().clone().add(getRandomOffset(), 1.0D, getRandomOffset());
        this.world.spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setMarker(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Utils.color("&c" + this.formatter.format(damage)));
            this.indicators.put(armorStand, Integer.valueOf(30));
        });
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!this.entities.containsKey(event.getEntity()))
            return;
        event.setDroppedExp(0);
        event.getDrops().clear();
        this.entities.remove(event.getEntity()).tryDropLoot(event.getEntity().getLocation());
    }

    @Override
    public void onDisable() {
        UniqueAbilities();
        CCStats.save();
        CCMagic.save();
        CCSkills.save();
        CCPlayerInfo.save();
        SQL.Disconect();
    }
    int count = 0;
    int count1 = 0;

    public static Logger getPluginLogger() {
        return logger;
    }
    public void Bar(){
        new BukkitRunnable() {
            @Override

            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){

                    String nick = p.getName();
                    int MPRn =  CCother.get().getInt(nick+".MP");
                    int MPStat = CCStats.get().getInt(nick+".Intelligence");
                    int MPRegen = CCStats.get().getInt(nick+".Wisdom");

                    int MAXMPset = (MPStat*5)+30;
                    CCother.get().set(nick+".MAXMP",MAXMPset);
                    CCother.save();

                    if(!(MPRn>=MAXMPset)){
                        if(MPRegen!=1){
                            CCother.get().set(nick+".MP",(MPRn+(MPRegen/2)));
                            CCother.save();
                        }else{
                            CCother.get().set(nick+".MP",(MPRn+(1)));
                            CCother.save();
                        }
                    }
                    if(MAXMPset<MPRn){
                        CCother.get().set(nick+".MP",MAXMPset);
                        CCother.save();
                    }


                    int HPRn = (int) p.getHealth();
                    int HPRN = HPRn*5;
                    CCother.get().set(nick+".HP",HPRN);
                    CCother.save();

                    int HPSTAT = CCStats.get().getInt(nick+".Health");
                    if(HPSTAT!=1){
                        CCother.get().set(nick+".MAXHP",(100+(HPSTAT*2)));
                    }else{
                        CCother.get().set(nick+".MAXHP",(100));

                    }
                    int HpMax = CCother.get().getInt(nick+".MAXHP");
                    CCother.save();


                    p.setMaxHealth(HpMax/5);

                    CCother.reload();

                    CCother.save();
                    int MP = CCother.get().getInt(nick+".MP");
                    int MAXMP = CCother.get().getInt(nick+".MAXMP");

                    int MAXHP = CCother.get().getInt(nick+".MAXHP");
                    int HPRNN = (int) p.getHealth()*5;
                    CCother.get().set(nick+".HP",HPRNN);
                    CCother.save();
                    int HP = CCother.get().getInt(nick+".HP");
                    if(MAXHP<HPRNN){
                        CCother.get().set(nick+".HP",MAXHP);
                        CCother.save();
                    }
                    int count = CCother.get().getInt(nick+".count");
                        if(p.getHealth()!=0){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(("Mana: "+ ChatColor.BLUE + MP+"/"+MAXMP +ChatColor.WHITE+ " HP: "+ ChatColor.RED +(HP)+"/"+MAXHP )));
                        }if(p.getHealth()==0){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(("Mana: "+ ChatColor.BLUE + MP+"/"+MAXMP +ChatColor.WHITE+ " HP: "+ ChatColor.RED +(HP)+"/"+MAXHP )));

                        }



                    int Agility = CCStats.get().getInt(nick+".Agility");
                    p.setWalkSpeed((float) (0.2 + Agility/20*0.02*2));

                    int lvlBar = CCother.get().getInt(nick+".lvlBar");
                    if(lvlBar==1){
                        BossBar bossBar = Bukkit.createBossBar(
                                ChatColor.DARK_PURPLE + "Visit our Website! " + ChatColor.LIGHT_PURPLE + "...", BarColor.PURPLE, BarStyle.SEGMENTED_20);
                    }
                }

            }

        }.runTaskTimerAsynchronously(this,0,10);
    }



    public void UniqueAbilities(){
        for(Player p : Bukkit.getOnlinePlayers()){
            String nick = p.getName();
            String UniqueAbility = CCSkills.get().getString(nick+".Skills.Unique");
            listP = UniqueAbilityWork.playersI;

            if(UniqueAbility.contains("Power of Giant")){
                if(UniqueAbility.contains("Power of Giant I - Tier II")){
                    int Strength = CCStats.get().getInt(nick+".Strength");
                    CCStats.get().set(nick+".Strength",Strength-5);
                    CCStats.save();
                }else if(UniqueAbility.contains("Power of Giant II - Tier II")){
                    int Strength = CCStats.get().getInt(nick+".Strength");
                    CCStats.get().set(nick+".Strength",Strength-10);
                    CCStats.save();
                }else if(UniqueAbility.contains("Power of Giant III - Tier II")){
                    int Strength = CCStats.get().getInt(nick+".Strength");
                    CCStats.get().set(nick+".Strength",Strength-20);
                    CCStats.save();
                }
            }

            //Lucker
            if(UniqueAbility.contains("Lucker")){
                if(UniqueAbility.contains("Lucker I - Tier I")){
                    int Luck = CCStats.get().getInt(nick+".Luck");
                    CCStats.get().set(nick+".Luck",Luck-5);
                    CCStats.save();
                }else if(UniqueAbility.contains("Lucker II - Tier I")){
                    int Luck = CCStats.get().getInt(nick+".Luck");
                    CCStats.get().set(nick+".Luck",Luck-10);
                    CCStats.save();
                }else if(UniqueAbility.contains("Lucker III - Tier I")){
                    int Luck = CCStats.get().getInt(nick+".Luck");
                    CCStats.get().set(nick+".Luck",Luck-20);
                    CCStats.save();
                }
            }
            //Mana Affinity I- Tier III
            if(UniqueAbility.contains("Mana Affinity")){
                if(UniqueAbility.contains("Mana Affinity I- Tier III")){
                    int Wisdom = CCStats.get().getInt(nick+".Wisdom");
                    int Intel = CCStats.get().getInt(nick+".Intelligence");
                    CCStats.get().set(nick+".Wisdom",Wisdom-3);
                    CCStats.get().set(nick+".Intelligence",Intel-3);
                    CCStats.save();
                }else if(UniqueAbility.contains("Mana Affinity II- Tier III")){
                    int Wisdom = CCStats.get().getInt(nick+".Wisdom");
                    int Intel = CCStats.get().getInt(nick+".Intelligence");
                    CCStats.get().set(nick+".Wisdom",Wisdom-5);
                    CCStats.get().set(nick+".Intelligence",Intel-5);
                    CCStats.save();
                }else if(UniqueAbility.contains("Mana Affinity III- Tier III")){
                    int Wisdom = CCStats.get().getInt(nick+".Wisdom");
                    int Intel = CCStats.get().getInt(nick+".Intelligence");
                    CCStats.get().set(nick+".Wisdom",Wisdom-10);
                    CCStats.get().set(nick+".Intelligence",Intel-5);
                    CCStats.save();
                }
            }
            //Tank
            if(UniqueAbility.contains("Tank")){
                if(UniqueAbility.contains("Tank I - Tier I")){
                    int MAXHP = CCother.get().getInt(nick+".MAXHP");
                    CCother.get().set(nick+".MAXHP",MAXHP-5);
                    CCother.save();
                    CCStats.save();
                }else if(UniqueAbility.contains("Tank II - Tier I")){
                    int MAXHP = CCother.get().getInt(nick+".MAXHP");
                    CCother.get().set(nick+".MAXHP",MAXHP-10);
                    CCother.save();
                    CCStats.save();
                }else if(UniqueAbility.contains("Tank III - Tier I")){
                    int MAXHP = CCother.get().getInt(nick+".MAXHP");
                    CCother.get().set(nick+".MAXHP",MAXHP-20);
                    CCother.save();
                    CCStats.save();
                }
            }
        }

    }
}
