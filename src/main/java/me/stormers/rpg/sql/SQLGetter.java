package me.stormers.rpg.sql;

import me.stormers.rpg.Main;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private final Main plugin;

    public SQLGetter(Main plugin) {
        this.plugin = plugin;
    }

    public void MakeTable() {
        PreparedStatement ps;
        PreparedStatement ps2;
        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE  IF NOT EXISTS  PlayerData" + "(NAME VARCHAR(100), UUID VARCHAR(100), GlobalLevel INT(100),PRIMARY KEY (NAME))");
            ps2 = plugin.SQL.getConnection().prepareStatement("CREATE TABLE  IF NOT EXISTS  Skills" + "(NAME VARCHAR(100), UUID VARCHAR(100), Agility INT(100), Strenght INT(100),Health INT(100),  ,PRIMARY KEY (NAME))");
            ps.executeUpdate();
            ps2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreatePlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM PlayerData WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            results.next();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INFO PlayerData" + "(NAME, UUID) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM PlayerData WHERE  UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet results = ps.executeQuery();
            return results.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void AddStrenght(UUID uuid, int Points) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE SKills SET level=? WHERE UUID=?");
            ps.setInt(1, Points);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {

        }
    }

    public void AddAgility(UUID uuid, int Points) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE SKills SET level=? WHERE UUID=?");
            ps.setInt(1, Points);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {

        }
    }

    public void AddWoodCutting(UUID uuid, int Points) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE SKills SET level=? WHERE UUID=?");
            ps.setInt(1, Points);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {

        }
    }






}
