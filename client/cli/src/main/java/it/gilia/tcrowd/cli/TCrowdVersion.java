package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
import it.gilia.tcrowd.utils.VersionInfo;

@Command(name = "--version",
        description = "Show version of t-crowd")
public class TCrowdVersion implements TCrowdCommand{

    @Override
    public void run() {
        //String version = getClass().getPackage().getImplementationVersion();

        VersionInfo versionInfo = VersionInfo.getVersionInfo();

        System.out.println(String.format("t-crowd version %s", versionInfo.toString()));
    }
}
