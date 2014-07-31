package com.bitpieces.stage.scheduled;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.tools.CoinbaseTools;
import com.bitpieces.shared.tools.DBActions;
import com.bitpieces.shared.tools.Tools;
import com.bitpieces.shared.tools.WebCommon;
import com.coinbase.api.Coinbase;

public class UpdateTransactionStatuses implements Job {

	public static void main(String[] args) {
		Properties prop = Tools.loadProperties(DataSources.STAGE_DB_PROP);
		Coinbase cb = CoinbaseTools.setupCoinbase(DataSources.COINBASE_PROP);
		
		WebCommon.dbInit(prop);
		
		DBActions.updateTransactionStatuses(cb);
		
		WebCommon.dbClose();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		main(null);
		
	}
	
}
