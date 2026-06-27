
package org.drip.dynamics.lmm;

import org.drip.analytics.definition.LatentStateStatic;
import org.drip.dynamics.evolution.LSQMPointRecord;
import org.drip.dynamics.evolution.LSQMPointUpdate;
import org.drip.numerical.common.NumberUtil;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.identifier.FundingLabel;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * Copyright (C) 2025 Lakshmi Krishnamurthy
 * Copyright (C) 2024 Lakshmi Krishnamurthy
 * Copyright (C) 2023 Lakshmi Krishnamurthy
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>ContinuousForwardRateUpdate</i> contains the Instantaneous Snapshot of the Evolving Discount Latent
 * 	State Quantification Metrics Updated using the Continuously Compounded Forward Rate Dynamics.
 *
 *	<br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): <i>Log-normality of Rates and Term Structure
 *  			Models</i> <b>The University of New South Wales</b>
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): <i>Nominal Annual Rates and Log-normal Volatility Structure</i> <b>The
 *  			University of New South Wales</b>
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics
 * 				<i>Mathematical Finance</i> <b>7 (2)</b> 127-155
 *  	</li>
 *  </ul>
 *
 * 	It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct an Instance of <i>ContinuousForwardRateUpdate</i></li>
 * 		<li>Retrieve the Continuously Compounded Forward Rate</li>
 * 		<li>Retrieve the Continuously Compounded Forward Rate Increment</li>
 * 		<li>Retrieve the Spot Rate</li>
 * 		<li>Retrieve the Spot Rate Increment</li>
 * 		<li>Retrieve the Discount Factor</li>
 * 		<li>Retrieve the Discount Factor Increment</li>
 * 		<li>Retrieve the Initial D {Continuously Compounded Forward Rate} / DX</li>
 * 		<li>Retrieve the Terminal D {Continuously Compounded Forward Rate} / DX</li>
 * </ul>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/lmm/README.md">LMM Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousForwardRateUpdate
	extends LSQMPointUpdate
{
	private ForwardLabel _forwardLabel = null;
	private FundingLabel _fundingLabel = null;
	private double _dContinuousForwardDXInitial = Double.NaN;
	private double _dContinuousForwardDXTerminal = Double.NaN;

	/**
	 * Construct an Instance of <i>ContinuousForwardRateUpdate</i>
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param forwardLabel The Forward Latent State Label
	 * @param initialDate The Initial Date
	 * @param finalDate The Final Date
	 * @param targetPointDate The Target Point Date
	 * @param continuousForwardRate The Continuously Compounded Forward Rate
	 * @param continuousForwardRateIncrement The Continuously Compounded Forward Rate Increment
	 * @param spotRate The Spot Rate
	 * @param spotRateIncrement The Spot Rate Increment
	 * @param discountFactor The Discount Factor
	 * @param discountFactorIncrement The Discount Factor Increment
	 * @param dContinuousForwardDXInitial Initial D {Continuously Compounded Forward Rate} / DX
	 * @param dContinuousForwardDXTerminal Terminal D {Continuously Compounded Forward Rate} / DX
	 * 
	 * @return Instance of <i>ContinuousForwardRateUpdate</i>
	 */

	public static final ContinuousForwardRateUpdate Create (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int initialDate,
		final int finalDate,
		final int targetPointDate,
		final double continuousForwardRate,
		final double continuousForwardRateIncrement,
		final double spotRate,
		final double spotRateIncrement,
		final double discountFactor,
		final double discountFactorIncrement,
		final double dContinuousForwardDXInitial,
		final double dContinuousForwardDXTerminal)
	{
		LSQMPointRecord snapshotLSQMPointRecord = new LSQMPointRecord();

		if (!snapshotLSQMPointRecord.setStateQuantificationMetric (
			forwardLabel,
			LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE,
			continuousForwardRate
		))
		{
			return null;
		}

		if (!snapshotLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
			spotRate
		))
		{
			return null;
		}

		if (!snapshotLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
			discountFactor
		))
		{
			return null;
		}

		LSQMPointRecord incrementLSQMPointRecord = new LSQMPointRecord();

		if (!incrementLSQMPointRecord.setStateQuantificationMetric (
			forwardLabel,
			LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE,
			continuousForwardRateIncrement
		))
		{
			return null;
		}

		if (!incrementLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
			spotRateIncrement
		))
		{
			return null;
		}

		if (!incrementLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
			discountFactorIncrement
		))
		{
			return null;
		}

		try {
			return new ContinuousForwardRateUpdate (
				fundingLabel,
				forwardLabel,
				initialDate,
				finalDate,
				targetPointDate,
				snapshotLSQMPointRecord,
				incrementLSQMPointRecord,
				dContinuousForwardDXInitial,
				dContinuousForwardDXTerminal
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ContinuousForwardRateUpdate (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int initialDate,
		final int finalDate,
		final int viewDate,
		final LSQMPointRecord snapshotLSQMPointRecord,
		final LSQMPointRecord incrementLSQMPointRecord,
		final double dContinuousForwardDXInitial,
		final double dContinuousForwardDXTerminal)
		throws Exception
	{
		super (initialDate, finalDate, viewDate, snapshotLSQMPointRecord, incrementLSQMPointRecord);

		if (null == (_fundingLabel = fundingLabel) ||
			null == (_forwardLabel = forwardLabel) ||
			!NumberUtil.IsValid (_dContinuousForwardDXTerminal = dContinuousForwardDXTerminal) ||
			!NumberUtil.IsValid (_dContinuousForwardDXInitial = dContinuousForwardDXInitial))
		{
			throw new Exception ("ContinuousForwardRateUpdate Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Continuously Compounded Forward Rate
	 * 
	 * @return The Continuously Compounded Forward Rate
	 * 
	 * @throws Exception Thrown if the Continuously Compounded Forward Rate is not available
	 */

	public double continuousForwardRate()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Continuously Compounded Forward Rate Increment
	 * 
	 * @return The Continuously Compounded Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Continuously Compounded Forward Rate Increment is not available
	 */

	public double continuousForwardRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Spot Rate
	 * 
	 * @return The Spot Rate
	 * 
	 * @throws Exception Thrown if the Spot Rate is not available
	 */

	public double spotRate()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE
		);
	}

	/**
	 * Retrieve the Spot Rate Increment
	 * 
	 * @return The Spot Rate Increment
	 * 
	 * @throws Exception Thrown if the Spot Rate Increment is not available
	 */

	public double spotRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE
		);
	}

	/**
	 * Retrieve the Discount Factor
	 * 
	 * @return The Discount Factor
	 * 
	 * @throws Exception Thrown if the Discount Factor is not available
	 */

	public double discountFactor()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}

	/**
	 * Retrieve the Discount Factor Increment
	 * 
	 * @return The Discount Factor Increment
	 * 
	 * @throws Exception Thrown if the Discount Factor Increment is not available
	 */

	public double discountFactorIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}

	/**
	 * Retrieve the Initial D {Continuously Compounded Forward Rate} / DX
	 * 
	 * @return The Initial D {Continuously Compounded Forward Rate} / DX
	 */

	public double dContinuousForwardDXInitial()
	{
		return _dContinuousForwardDXInitial;
	}

	/**
	 * Retrieve the Terminal D {Continuously Compounded Forward Rate} / DX
	 * 
	 * @return The Terminal D {Continuously Compounded Forward Rate} / DX
	 */

	public double dContinuousForwardDXTerminal()
	{
		return _dContinuousForwardDXTerminal;
	}
}
