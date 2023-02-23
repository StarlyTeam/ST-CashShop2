package net.starly.cashshop.util;

import lombok.Getter;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.exception.UnSupportedVersionException;
import org.bukkit.Server;
import java.util.Arrays;
import java.util.Optional;

public class VersionController {

    public enum Version {
        v1_12_R1("1.12", false),
        v1_13_R1("1.13", true),
        v1_14_R1("1.14", true),
        v1_15_R1("1.15", true),
        v1_16_R1("1.16", true),
        v1_17_R1("1.17", true),
        v1_18_R1("1.18", true),
        v1_19_R2("1.19", true);

        @Getter private final String v;
        @Getter private final String version = name();
        @Getter private final boolean highVersion;
        Version(String v, boolean highVersion) {
            this.v = v;
            this.highVersion = highVersion;
        }
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

    @Getter private final Version version;

    private VersionController(Server server) throws UnSupportedVersionException {
        version = checkVersions(server);
    }

    private Version checkVersions(Server server) throws UnSupportedVersionException {
        Optional<Version> versionFilter = Arrays.stream(Version.values()).filter(it->server.getVersion().contains(it.v)).findFirst();
        if(versionFilter.isPresent()) return versionFilter.get();
        else throw new UnSupportedVersionException(server.getVersion());
    }

}
