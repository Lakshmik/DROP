
package org.drip.dynamics.hjm;

import org.drip.analytics.definition.LatentStateStatic;
import org.drip.dynamics.evolution.LSQMPointRecord;
import org.drip.dynamics.evolution.LSQMPointUpdate;
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
 * <i>ShortForwardRateUpdate</i> contains the Instantaneous Snapshot of the Evolving Discount Latent State
 * 	Quantification Metrics. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct an Instance of <i>ShortForwardRateUpdate</i></li>
 * 		<li>Retrieve the Instantaneous Forward Rate</li>
 * 		<li>Retrieve the Instantaneous Forward Rate Increment</li>
 * 		<li>Retrieve the LIBOR Forward Rate</li>
 * 		<li>Retrieve the LIBOR Forward Rate Increment</li>
 * 		<li>Retrieve the Shifted LIBOR Forward Rate</li>
 * 		<li>Retrieve the Shifted LIBOR Forward Rate Increment</li>
 * 		<li>Retrieve the Short Rate</li>
 * 		<li>Retrieve the Short Rate Increment</li>
 * 		<li>Retrieve the Compounded Short Rate</li>
 * 		<li>Retrieve the Compounded Short Rate Increment</li>
 * 		<li>Retrieve the Price</li>
 * 		<li>Retrieve the Price Increment</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/hjm/README.md">HJM Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ShortForwardRateUpdate
	extends LSQMPointUpdate
{
	private ForwardLabel _forwardLabel = null;
	private FundingLabel _fundingLabel = null;

	/**
	 * Construct an Instance of <i>ShortForwardRateUpdate</i>
	 * 
	 * @param lslFunding The Funding Latent State Label
	 * @param lslForward The Forward Latent State Label
	 * @param iInitialDate The Initial Date
	 * @param iFinalDate The Final Date
	 * @param iTargetPointDate The Target Point Date
	 * @param dblInstantaneousForwardRate The Instantaneous Forward Rate
	 * @param dblInstantaneousForwardRateIncrement The Instantaneous Forward Rate Increment
	 * @param dblLIBORForwardRate The LIBOR Forward Rate
	 * @param dblLIBORForwardRateIncrement The LIBOR Forward Rate Increment
	 * @param dblShiftedLIBORForwardRate The Shifted LIBOR Forward Rate
	 * @param dblShiftedLIBORForwardRateIncrement The Shifted LIBOR Forward Rate Increment
	 * @param dblShortRate The Short Rate
	 * @param dblShortRateIncrement The Short Rate Increment
	 * @param dblCompoundedShortRate The Compounded Short Rate
	 * @param dblCompoundedShortRateIncrement The Compounded Short Rate Increment
	 * @param dblPrice The Price
	 * @param dblPriceIncrement The Price Increment
	 * 
	 * @return Instance of ShortForwardRateUpdate
	 */

	public static final ShortForwardRateUpdate Create (
		final FundingLabel lslFunding,
		final ForwardLabel lslForward,
		final int iInitialDate,
		final int iFinalDate,
		final int iTargetPointDate,
		final double dblInstantaneousForwardRate,
		final double dblInstantaneousForwardRateIncrement,
		final double dblLIBORForwardRate,
		final double dblLIBORForwardRateIncrement,
		final double dblShiftedLIBORForwardRate,
		final double dblShiftedLIBORForwardRateIncrement,
		final double dblShortRate,
		final double dblShortRateIncrement,
		final double dblCompoundedShortRate,
		final double dblCompoundedShortRateIncrement,
		final double dblPrice,
		final double dblPriceIncrement)
	{
		LSQMPointRecord lrSnapshot = new
			LSQMPointRecord();

		if (!lrSnapshot.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_ZERO_RATE, dblShortRate))
			return null;

		if (!lrSnapshot.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_COMPOUNDED_SHORT_RATE,
				dblCompoundedShortRate))
			return null;

		if (!lrSnapshot.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR, dblPrice))
			return null;

		if (!lrSnapshot.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_FORWARD_RATE, dblLIBORForwardRate))
			return null;

		if (!lrSnapshot.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_SHIFTED_FORWARD_RATE,
				dblShiftedLIBORForwardRate))
			return null;

		if (!lrSnapshot.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_FORWARD_RATE,
				dblInstantaneousForwardRate))
			return null;

		LSQMPointRecord lrIncrement = new
			LSQMPointRecord();

		if (!lrIncrement.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_ZERO_RATE, dblShortRateIncrement))
			return null;

		if (!lrIncrement.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_COMPOUNDED_SHORT_RATE,
				dblCompoundedShortRateIncrement))
			return null;

		if (!lrIncrement.setStateQuantificationMetric (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR, dblPriceIncrement))
			return null;

		if (!lrIncrement.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_FORWARD_RATE,
				dblLIBORForwardRateIncrement))
			return null;

		if (!lrIncrement.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_SHIFTED_FORWARD_RATE,
				dblShiftedLIBORForwardRateIncrement))
			return null;

		if (!lrIncrement.setStateQuantificationMetric (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_FORWARD_RATE,
				dblInstantaneousForwardRateIncrement))
			return null;

		try {
			return new ShortForwardRateUpdate (lslFunding, lslForward, iInitialDate, iFinalDate,
				iTargetPointDate, lrSnapshot, lrIncrement);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ShortForwardRateUpdate (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int initialDate,
		final int finalDate,
		final int viewDate,
		final LSQMPointRecord snapshotLSQMPointRecord,
		final LSQMPointRecord incrementLSQMPointRecord)
		throws Exception
	{
		super (initialDate, finalDate, viewDate, snapshotLSQMPointRecord, incrementLSQMPointRecord);

		if (null == (_fundingLabel = fundingLabel) || null == (_forwardLabel = forwardLabel)) {
			throw new Exception ("ShortForwardRateUpdate Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Instantaneous Forward Rate
	 * 
	 * @return The Instantaneous Forward Rate
	 * 
	 * @throws Exception Thrown if the Instantaneous Forward Rate is not available
	 */

	public double instantaneousForwardRate()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_INSTANTANEOUS_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Instantaneous Forward Rate Increment
	 * 
	 * @return The Instantaneous Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Instantaneous Forward Rate Increment is not available
	 */

	public double instantaneousForwardRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_INSTANTANEOUS_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the LIBOR Forward Rate
	 * 
	 * @return The LIBOR Forward Rate
	 * 
	 * @throws Exception Thrown if the Forward Rate is not available
	 */

	public double liborForwardRate()
		throws Exception
	{
		return snapshot().quantificationMetric (_forwardLabel, LatentStateStatic.FORWARD_QM_FORWARD_RATE);
	}

	/**
	 * Retrieve the LIBOR Forward Rate Increment
	 * 
	 * @return The LIBOR Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Forward Rate Increment is not available
	 */

	public double liborForwardRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (_forwardLabel, LatentStateStatic.FORWARD_QM_FORWARD_RATE);
	}

	/**
	 * Retrieve the Shifted LIBOR Forward Rate
	 * 
	 * @return The Shifted LIBOR Forward Rate
	 * 
	 * @throws Exception Thrown if the Shifted Forward Rate is not available
	 */

	public double shiftedLIBORForwardRate()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_SHIFTED_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Shifted LIBOR Forward Rate Increment
	 * 
	 * @return The Shifted LIBOR Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Shifted Forward Rate Increment is not available
	 */

	public double shiftedLIBORForwardRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_SHIFTED_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Short Rate
	 * 
	 * @return The Short Rate
	 * 
	 * @throws Exception Thrown if the Short Rate is not available
	 */

	public double shortRate()
		throws Exception
	{
		return snapshot().quantificationMetric (_fundingLabel, LatentStateStatic.DISCOUNT_QM_ZERO_RATE);
	}

	/**
	 * Retrieve the Short Rate Increment
	 * 
	 * @return The Short Rate Increment
	 * 
	 * @throws Exception Thrown if the Short Rate Increment is not available
	 */

	public double shortRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (_fundingLabel, LatentStateStatic.DISCOUNT_QM_ZERO_RATE);
	}

	/**
	 * Retrieve the Compounded Short Rate
	 * 
	 * @return The Compounded Short Rate
	 * 
	 * @throws Exception Thrown if the Compounded Short Rate is not available
	 */

	public double compoundedShortRate()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_COMPOUNDED_SHORT_RATE
		);
	}

	/**
	 * Retrieve the Compounded Short Rate Increment
	 * 
	 * @return The Compounded Short Rate Increment
	 * 
	 * @throws Exception Thrown if the Compounded Short Rate Increment is not available
	 */

	public double compoundedShortRateIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_COMPOUNDED_SHORT_RATE
		);
	}

	/**
	 * Retrieve the Price
	 * 
	 * @return The Price
	 * 
	 * @throws Exception Thrown if the Price is not available
	 */

	public double price()
		throws Exception
	{
		return snapshot().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}

	/**
	 * Retrieve the Price Increment
	 * 
	 * @return The Price Increment
	 * 
	 * @throws Exception Thrown if the Price Increment is not available
	 */

	public double priceIncrement()
		throws Exception
	{
		return increment().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}
}
