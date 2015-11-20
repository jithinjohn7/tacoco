package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spideruci.tacoco.db.CreateSQLiteDB;

public class TacocoAnalyzer extends AbstractRuntimeAnalyzer {
	
	private String name;
	private String outDir;
	private File exec;
	private String sutHome;
	
	@Override
	public void setup() {
		super.setup();
		name = readOptionalArgumentValue(PROJECT, this.buildProbe.getId());
		outDir = readArgumentValue(OUTDIR);
		if(!new File(outDir).exists()) {
			throw new RuntimeException("specified output directory does not exist: " + outDir);
		}
		exec = new File(outDir, "tacoco.exec");
		sutHome = readArgumentValue(SUT);
	}
	
	@Override
	public void analyze() {
		List<String> klassesStrings = this.buildProbe.getTestClasses();
		List<Class<?>> klasses = new ArrayList<>();
		for(String klassString : klassesStrings) {
			Class<?> klass;
			try {
				klass = Class.forName(klassString);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			klasses.add(klass);
		}
		this.runTests(klasses);
	}
	
	@Override
	public void printAnalysisSummary() {
		super.printAnalysisSummary();
		try {
			String dbFile = outDir + File.separator + name + ".db";
			CreateSQLiteDB.dump(dbFile, sutHome, exec.toString());
			exec.renameTo(new File(outDir, name + ".exec"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	@Override
	public String getName() {
		return "TACOCO";
	}

}
