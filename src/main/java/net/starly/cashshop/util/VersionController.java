package net.starly.cashshop.util;

import lombok.Getter;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.exception.UnSupportedVersionException;
import org.bukkit.Server;
import java.util.Arrays;
import java.util.Optional;

public class VersionController {

    public enum Version {
        v1_12_R1("1.12"),
        v1_13_R1("1.13"),
        v1_14_R1("1.14"),
        v1_15_R1("1.15"),
        v1_16_R1("1.16"),
        v1_17_R1("1.17"),
        v1_18_R1("1.18"),
        v1_19_R2("1.19");


        @Getter private final String v;
        @Getter private final String v2;
        @Getter private final String version = name();
        Version(String v) { this.v = v; v2 = null; }
        Version(String v, String v2) { this.v = v; this.v2 = v2; }
    }

    private static VersionController instance;
    public static VersionController getInstance() {
        try {
            if (instance == null) instance = new VersionController(CashShopMain.getPlugin().getServer());
            return instance;
        } catch (Exception ignored) {
            return null;
        }
    }
    @Getter private Version version = null;

    private VersionController(Server server) throws UnSupportedVersionException, ClassNotFoundException {
        checkVersions(server);
        if(version == null) throw new UnSupportedVersionException(version.v);
    }

    private void checkVersions(Server server) throws UnSupportedVersionException {
        Optional<Version> versionFilter = Arrays.stream(Version.values()).filter(it->{
            if(server.getVersion().contains(it.v)) {
                if(it.v2 != null) return server.getVersion().contains(it.v2);
                else return true;
            } else return false;
        }).findFirst();
        versionFilter.ifPresent(value -> this.version = value);
    }

    private String getPackageVersion(Package dir) {
        String name = dir.getName();
        String[] args = name.split("\\.");
        return args[args.length-1].replace("_", ".").replace("v", "");
    }

}
