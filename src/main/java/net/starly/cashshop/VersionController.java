package net.starly.cashshop;

import lombok.Getter;
import lombok.NonNull;
import net.starly.cashshop.exception.UnSupportedVersionException;
import net.starly.cashshop.version.ItemStackUtility;
import net.starly.cashshop.version.v1_12.ItemStackUtility12;
import org.bukkit.Server;

public class VersionController {

    private static VersionController instance;
    public static VersionController getInstance() {
        try {
            if (instance == null) instance = new VersionController(CashShopMain.getPlugin().getServer());
            return instance;
        } catch (Exception ignored) {
            return null;
        }
    }
    @Getter private final String version;
    @Getter private ItemStackUtility itemStackUtility;

    private VersionController(Server server) throws UnSupportedVersionException, ClassNotFoundException {
        version = server.getVersion();
        if(version.contains("1.12")) itemStackUtility = new ItemStackUtility12();

        if(itemStackUtility == null) throw new UnSupportedVersionException(version);
    }

    private String getPackageVersion(Package dir) {
        String name = dir.getName();
        String[] args = name.split("\\.");
        return args[args.length-1].replace("_", ".").replace("v", "");
    }

}
