
package org.drip.dynamics.sabr;

import org.drip.dynamics.evolution.LSQMPointUpdate;
import org.drip.dynamics.evolution.PointStateEvolver;
import org.drip.sequence.random.UnivariateSequenceGenerator;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>StochasticVolatilityStateEvolver</i> provides the SABR Stochastic Volatility Evolution Dynamics.
 *
 *	<br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/README.md">SABR Based Latent State Evolution</a></li>
 *  </ul>
 *
 * @author Lakshmi Krishnamurthy
 */

public class StochasticVolatilityStateEvolver
	implements PointStateEvolver
{
	private ForwardLabel _forwardLabel = null;
	private double _idiosyncraticVolatilityRho = Double.NaN;
	private ForwardProcessSetting _forwardProcessSetting = null;
	private UnivariateSequenceGenerator _forwardUnivariateSequenceGenerator = null;
	private UnivariateSequenceGenerator _forwardVolatilityUnivariateSequenceGenerator = null;

	/**
	 * <i>StochasticVolatilityStateEvolver</i> Constructor
	 * 
	 * @param forwardLabel The Forward Rate Latent State Label
	 * @param forwardProcessSetting <i>ForwardProcessSetting</i> Instance
	 * @param forwardUnivariateSequenceGenerator The Forward Rate Univariate Sequence Generator
	 * @param forwardVolatilityUnivariateSequenceGenerator The Idiosyncratic Component Forward Rate
	 * 		Volatility Univariate Sequence Generator
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public StochasticVolatilityStateEvolver (
		final ForwardLabel forwardLabel,
		final ForwardProcessSetting forwardProcessSetting,
		final UnivariateSequenceGenerator forwardUnivariateSequenceGenerator,
		final UnivariateSequenceGenerator forwardVolatilityUnivariateSequenceGenerator)
		throws Exception
	{
		if (null == (_forwardLabel = forwardLabel) ||
			null == (_forwardProcessSetting = forwardProcessSetting) ||
			null == (_forwardUnivariateSequenceGenerator = forwardUnivariateSequenceGenerator) ||
			null == (
				_forwardVolatilityUnivariateSequenceGenerator = forwardVolatilityUnivariateSequenceGenerator
			)
		)
		{
			throw new Exception ("StochasticVolatilityStateEvolver Contructor => Invalid Inputs");
		}

		double rho = forwardProcessSetting.rho();

		_idiosyncraticVolatilityRho = Math.sqrt (1. - rho * rho);
	}

	/**
	 * Retrieve the <i>ForwardProcessSetting</i> Instance
	 * 
	 * @return The <i>ForwardProcessSetting</i> Instance
	 */

	public ForwardProcessSetting forwardProcessSetting()
	{
		return _forwardProcessSetting;
	}

	/**
	 * Retrieve the Forward Label
	 * 
	 * @return The Forward Label
	 */

	public ForwardLabel forwardLabel()
	{
		return _forwardLabel;
	}

	/**
	 * Retrieve the Forward Univariate Random Variable Generator Sequence
	 * 
	 * @return The Forward Univariate Random Variable Generator Sequence
	 */

	public UnivariateSequenceGenerator forwardUnivariateSequenceGenerator()
	{
		return _forwardUnivariateSequenceGenerator;
	}

	/**
	 * Retrieve the Forward Volatility Univariate Random Variable Generator Sequence
	 * 
	 * @return Forward Volatility Univariate Random Variable Generator Sequence
	 */

	public UnivariateSequenceGenerator forwardVolatilityUnivariateSequenceGenerator()
	{
		return _forwardVolatilityUnivariateSequenceGenerator;
	}

	/**
	 * Evolve the Latent State and return the LSQM Point Update
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param spotTimeIncrement The Spot Time Increment
	 * @param previousLSQMPointUpdate The Previous LSQM Point Update
	 * 
	 * @return The LSQM Point Update
	 */

	@Override public LSQMPointUpdate evolve (
		final int spotDate,
		final int viewDate,
		final int spotTimeIncrement,
		final LSQMPointUpdate previousLSQMPointUpdate)
	{
		if (viewDate < spotDate ||
			null == previousLSQMPointUpdate || !(previousLSQMPointUpdate instanceof ForwardUpdate))
		{
			return null;
		}

		double forwardWander = _forwardUnivariateSequenceGenerator.random();

		ForwardUpdate previousForwardUpdate = (ForwardUpdate) previousLSQMPointUpdate;

		double annualizedIncrementSQRT = Math.sqrt (1. * spotTimeIncrement / 365.25);

		try {
			double forward = previousForwardUpdate.forward();

			double forwardVolatility = previousForwardUpdate.forwardVolatility();

			double forwardIncrement = forwardVolatility * _forwardProcessSetting.cFunction().c (
				forward + _forwardProcessSetting.shift()
			) * annualizedIncrementSQRT * forwardWander;

			double forwardRateVolatilityIncrement = _forwardProcessSetting.volVol() * forwardVolatility *
				annualizedIncrementSQRT * (
					_forwardProcessSetting.rho() * forwardWander +
					_idiosyncraticVolatilityRho * _forwardVolatilityUnivariateSequenceGenerator.random()
				);

			return ForwardUpdate.Create (
				_forwardLabel,
				spotDate,
				spotDate + spotTimeIncrement,
				viewDate,
				forward + forwardIncrement,
				forwardIncrement,
				forwardVolatility + forwardRateVolatilityIncrement,
				forwardRateVolatilityIncrement
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
