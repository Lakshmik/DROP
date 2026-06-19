
package org.drip.dynamics.hullwhite;

import org.drip.analytics.definition.LatentStateStatic;
import org.drip.dynamics.evolution.LSQMPointRecord;
import org.drip.dynamics.evolution.LSQMPointUpdate;
import org.drip.numerical.common.NumberUtil;
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
 * <i>ShortRateUpdate</i> records the Metrics associated with the Evolution of the Instantaneous Short Rate
 * 	from a Starting to the Terminal Date. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct an Instance of <i>ShortRateUpdate</i></li>
 * 		<li>Retrieve the Initial Short Rate</li>
 * 		<li>Retrieve the Realized Final Short Rate</li>
 * 		<li>Retrieve the Short Rate Increment</li>
 * 		<li>Retrieve the Expected Final Short Rate</li>
 * 		<li>Retrieve the Final Short Rate Variance</li>
 * 		<li>Compute the Zero Coupon Bond Price</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/hullwhite/README.md">Hull White Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ShortRateUpdate
	extends LSQMPointUpdate
{
	private FundingLabel _fundingLabel = null;
	private double _expectedFinalShortRate = Double.NaN;
	private double _finalShortRateVariance = Double.NaN;

	/**
	 * Construct an Instance of <i>ShortRateUpdate</i>
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param initialDate The Initial Date
	 * @param finalDate The Final Date
	 * @param targetPointDate The Target Point Date
	 * @param initialShortRate The Initial Short Rate
	 * @param realizedFinalShortRate The Realized Final Short Rate
	 * @param expectedFinalShortRate The Expected Final Short Rate
	 * @param finalShortRateVariance The Final Variance of the Short Rate
	 * @param zeroCouponBondPrice The Zero Coupon Bond Price
	 * 
	 * @return The <i>ShortRateUpdate</i> Instance
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public static final ShortRateUpdate Create (
		final FundingLabel fundingLabel,
		final int initialDate,
		final int finalDate,
		final int targetPointDate,
		final double initialShortRate,
		final double realizedFinalShortRate,
		final double expectedFinalShortRate,
		final double finalShortRateVariance,
		final double zeroCouponBondPrice)
		throws Exception
	{
		LSQMPointRecord snapshotLSQMPointRecord = new LSQMPointRecord();

		if (!snapshotLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
			realizedFinalShortRate
		))
		{
			return null;
		}

		if (!snapshotLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
			zeroCouponBondPrice
		))
		{
			return null;
		}

		LSQMPointRecord incrementLSQMPointRecord = new LSQMPointRecord();

		if (!incrementLSQMPointRecord.setStateQuantificationMetric (
			fundingLabel,
			LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
			realizedFinalShortRate - initialShortRate
		))
		{
			return null;
		}

		try {
			return new ShortRateUpdate (
				fundingLabel,
				initialDate,
				finalDate,
				targetPointDate,
				snapshotLSQMPointRecord,
				incrementLSQMPointRecord,
				expectedFinalShortRate,
				finalShortRateVariance
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ShortRateUpdate (
		final FundingLabel fundingLabel,
		final int initialDate,
		final int finalDate,
		final int viewDate,
		final LSQMPointRecord snapshotLSQMPointRecord,
		final LSQMPointRecord incrementLSQMPointRecord,
		final double expectedFinalShortRate,
		final double finalShortRateVariance)
		throws Exception
	{
		super (initialDate, finalDate, viewDate, snapshotLSQMPointRecord, incrementLSQMPointRecord);

		if (null == (_fundingLabel = fundingLabel) ||
			!NumberUtil.IsValid (_expectedFinalShortRate = expectedFinalShortRate) ||
			!NumberUtil.IsValid (_finalShortRateVariance = finalShortRateVariance))
		{
			System.out.println (_fundingLabel.fullyQualifiedName());

			System.out.println ("Final Short Rate: " + _expectedFinalShortRate);

			System.out.println ("Final Short Rate Variance: " + _finalShortRateVariance);

			throw new Exception ("ShortRateUpdate Constructor: Invalid Inputs!");
		}
	}

	/**
	 * Retrieve the Initial Short Rate
	 * 
	 * @return The Initial Short Rate
	 * 
	 * @throws Exception Thrown if the Initial Short Rate is not available
	 */

	public double initialShortRate()
		throws Exception
	{
		return realizedFinalShortRate() - shortRateIncrement();
	}

	/**
	 * Retrieve the Realized Final Short Rate
	 * 
	 * @return The Realized Final Short Rate
	 * 
	 * @throws Exception Thrown if the Realized Final Short Rate is not available
	 */

	public double realizedFinalShortRate()
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
	 * Retrieve the Expected Final Short Rate
	 * 
	 * @return The Expected Final Short Rate
	 */

	public double expectedFinalShortRate()
	{
		return _expectedFinalShortRate;
	}

	/**
	 * Retrieve the Final Short Rate Variance
	 * 
	 * @return The Final Short Rate Variance
	 */

	public double finalShortRateVariance()
	{
		return _finalShortRateVariance;
	}

	/**
	 * Compute the Zero Coupon Bond Price
	 * 
	 * @param finalInitialZeroRatio The Final-to-Initial Zero-Coupon Bond Price Ratio
	 * 
	 * @return The Zero Coupon Bond Price
	 * 
	 * @throws Exception Thrown if the Zero Coupon Bond Price cannot be computed
	 */

	public double zeroCouponBondPrice (
		final double finalInitialZeroRatio)
		throws Exception
	{
		if (!NumberUtil.IsValid (finalInitialZeroRatio)) {
			throw new Exception ("ShortRateUpdate::zeroCouponBondPrice => Invalid Inputs");
		}

		return finalInitialZeroRatio * snapshot().quantificationMetric (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}
}
