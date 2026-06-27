
package org.drip.dynamics.lmm;

import org.drip.analytics.definition.LatentStateStatic;
import org.drip.dynamics.evolution.LSQMCurveIncrement;
import org.drip.dynamics.evolution.LSQMCurveSnapshot;
import org.drip.dynamics.evolution.LSQMCurveUpdate;
import org.drip.spline.grid.Span;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.forward.ForwardCurve;
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
 * <i>BGMCurveUpdate</i> contains the Instantaneous Snapshot of the Evolving Discount Curve Latent State
 * 	Quantification Metrics Updated using the BGM LIBOR Update Dynamics. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct an Instance of <i>BGMCurveUpdate</i></li>
 * 		<li>Retrieve the LIBOR Forward Curve</li>
 * 		<li>Retrieve the LIBOR Forward Curve Increment Span</li>
 * 		<li>Retrieve the Instantaneous Continuously Compounded Forward Curve Increment Span</li>
 * 		<li>Retrieve the Instantaneous Effective Annual Forward Rate Span</li>
 * 		<li>Retrieve the Instantaneous Nominal Annual Forward Rate Span</li>
 * 		<li>Retrieve the Discount Factor Curve</li>
 * 		<li>Retrieve the Discount Factor Discount Curve Increment</li>
 * 		<li>Retrieve the Spot Rate Discount Curve Increment</li>
 * 		<li>Retrieve the Log-normal LIBOR Volatility Instance</li>
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

public class BGMCurveUpdate
	extends LSQMCurveUpdate
{
	private ForwardLabel _forwardLabel = null;
	private FundingLabel _fundingLabel = null;
	private LognormalLIBORVolatility _lognormalLIBORVolatility = null;

	/**
	 * Construct an Instance of <i>BGMCurveUpdate</i>
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param forwardLabel The Forward Latent State Label
	 * @param initialDate The Initial Date
	 * @param finalDate The Final Date
	 * @param forwardCurve The LIBOR Forward Curve Snapshot
	 * @param liborIncrementSpan The LIBOR Forward Curve Increment Span
	 * @param discountCurve The Discount Factor Discount Curve
	 * @param discountFactorIncrementSpan The Discount Factor Discount Curve Increment Span
	 * @param continuousForwardRateIncrementSpan The Continuous Forward Rate Discount Curve Increment Span
	 * @param spotRateIncrementSpan The Spot Rate Discount Curve Increment Span
	 * @param instantaneousEffectiveForwardSpan The Instantaneous Effective Forward Rate Span
	 * @param instantaneousNominalForwardSpan The Instantaneous Nominal Forward Rate Span
	 * @param lognormalLIBORVolatility The Log-normal LIBOR Rate Volatility
	 * 
	 * @return Instance of <i>BGMCurveUpdate</i>
	 */

	public static final BGMCurveUpdate Create (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int initialDate,
		final int finalDate,
		final ForwardCurve forwardCurve,
		final Span liborIncrementSpan,
		final MergedDiscountForwardCurve discountCurve,
		final Span discountFactorIncrementSpan,
		final Span continuousForwardRateIncrementSpan,
		final Span spotRateIncrementSpan,
		final Span instantaneousEffectiveForwardSpan,
		final Span instantaneousNominalForwardSpan,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
	{
		LSQMCurveSnapshot lsqmCurveSnapshot = new LSQMCurveSnapshot();

		if (!lsqmCurveSnapshot.setQuantificationMetricCurve (
			LatentStateStatic.FORWARD_QM_LIBOR_RATE,
			forwardCurve
		))
		{
			return null;
		}

		if (!lsqmCurveSnapshot.setQuantificationMetricCurve (
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
			discountCurve
		))
		{
			return null;
		}

		LSQMCurveIncrement lsqmCurveIncrement = new LSQMCurveIncrement();

		if (null != liborIncrementSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				forwardLabel,
				LatentStateStatic.FORWARD_QM_LIBOR_RATE,
				liborIncrementSpan
			)
		)
		{
			return null;
		}

		if (null != continuousForwardRateIncrementSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				forwardLabel,
				LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE,
				continuousForwardRateIncrementSpan
			)
		)
		{
			return null;
		}

		if (null != discountFactorIncrementSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				fundingLabel,
				LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
				discountFactorIncrementSpan
			)
		)
		{
			return null;
		}

		if (null != spotRateIncrementSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				fundingLabel,
				LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
				spotRateIncrementSpan
			)
		)
		{
			return null;
		}

		if (null != instantaneousEffectiveForwardSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				forwardLabel,
				LatentStateStatic.FORWARD_QM_INSTANTANEOUS_EFFECTIVE_FORWARD_RATE,
				instantaneousEffectiveForwardSpan
			)
		)
		{
			return null;
		}

		if (null != instantaneousNominalForwardSpan &&
			!lsqmCurveIncrement.setupQuantificationMetricSpan (
				forwardLabel,
				LatentStateStatic.FORWARD_QM_INSTANTANEOUS_NOMINAL_FORWARD_RATE,
				instantaneousNominalForwardSpan
			)
		)
		{
			return null;
		}

		try {
			return new BGMCurveUpdate (
				fundingLabel,
				forwardLabel,
				initialDate,
				finalDate,
				lsqmCurveSnapshot,
				lsqmCurveIncrement,
				lognormalLIBORVolatility
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private BGMCurveUpdate (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int initialDate,
		final int finalDate,
		final LSQMCurveSnapshot lsqmCurveSnapshot,
		final LSQMCurveIncrement lsqmCurveIncrement,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
		throws Exception
	{
		super (initialDate, finalDate, lsqmCurveSnapshot, lsqmCurveIncrement);

		if (null == (_fundingLabel = fundingLabel) ||
			null == (_forwardLabel = forwardLabel) ||
			null == (_lognormalLIBORVolatility = lognormalLIBORVolatility))
		{
			throw new Exception ("BGMCurveUpdate Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the LIBOR Forward Curve
	 * 
	 * @return The LIBOR Forward Curve
	 */

	public ForwardCurve forwardCurve()
	{
		return (ForwardCurve) snapshot().quantificationMetricCurve (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_LIBOR_RATE
		);
	}

	/**
	 * Retrieve the LIBOR Forward Curve Increment Span
	 * 
	 * @return The LIBOR Forward Curve Increment Span
	 */

	public Span forwardCurveIncrement()
	{
		return increment().span (_forwardLabel, LatentStateStatic.FORWARD_QM_LIBOR_RATE);
	}

	/**
	 * Retrieve the Instantaneous Continuously Compounded Forward Curve Increment Span
	 * 
	 * @return The Instantaneous Continuously Compounded Forward Curve Increment Span
	 */

	public Span continuousForwardRateIncrement()
	{
		return increment().span (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Instantaneous Effective Annual Forward Rate Span
	 * 
	 * @return The Instantaneous Effective Annual Forward Rate Span
	 */

	public Span instantaneousEffectiveForwardRate()
	{
		return increment().span (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_INSTANTANEOUS_EFFECTIVE_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Instantaneous Nominal Annual Forward Rate Span
	 * 
	 * @return The Instantaneous Nominal Annual Forward Rate Span
	 */

	public Span instantaneousNominalForwardRate()
	{
		return increment().span (
			_forwardLabel,
			LatentStateStatic.FORWARD_QM_INSTANTANEOUS_NOMINAL_FORWARD_RATE
		);
	}

	/**
	 * Retrieve the Discount Factor Curve
	 * 
	 * @return The Discount Factor Curve
	 */

	public MergedDiscountForwardCurve discountCurve()
	{
		return (MergedDiscountForwardCurve) snapshot().quantificationMetricCurve (
			_fundingLabel,
			LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR
		);
	}

	/**
	 * Retrieve the Discount Factor Discount Curve Increment
	 * 
	 * @return The Discount Factor Discount Curve Increment
	 */

	public Span discountCurveIncrement()
	{
		return increment().span (_fundingLabel, LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR);
	}

	/**
	 * Retrieve the Spot Rate Discount Curve Increment
	 * 
	 * @return The Spot Rate Discount Curve Increment
	 */

	public Span spotRateIncrement()
	{
		return increment().span (_fundingLabel, LatentStateStatic.DISCOUNT_QM_ZERO_RATE);
	}

	/**
	 * Retrieve the Log-normal LIBOR Volatility Instance
	 * 
	 * @return The Log-normal LIBOR Volatility Instance
	 */

	public LognormalLIBORVolatility lognormalLIBORVolatility()
	{
		return _lognormalLIBORVolatility;
	}
}
