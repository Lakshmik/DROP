
package org.drip.dynamics.hullwhite;

import org.drip.dynamics.evolution.LSQMPointUpdate;
import org.drip.dynamics.evolution.PointStateEvolver;
import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.sequence.random.UnivariateSequenceGenerator;
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
 * <i>SingleFactorStateEvolver</i> provides the Hull-White One-Factor Gaussian HJM Short Rate Dynamics
 * 	Implementation. It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>SingleFactorStateEvolver</i> Constructor</li>
 * 		<li>Retrieve the Funding Label</li>
 * 		<li>Retrieve Sigma</li>
 * 		<li>Retrieve A</li>
 * 		<li>Retrieve the Initial Instantaneous Forward Rate Term Structure</li>
 * 		<li>Retrieve the Random Sequence Generator</li>
 * 		<li>Calculate the Alpha</li>
 * 		<li>Calculate the Theta</li>
 * 		<li>Calculate the Short Rate Increment</li>
 * 		<li>Evolve the Latent State and return the LSQM Point Update</li>
 * 		<li>Generate the Metrics associated with the Transition that results from using a Trinomial Tree Using the Starting Node Metrics</li>
 * 		<li>Evolve the Trinomial Tree Sequence #1</li>
 * 		<li>Evolve the Trinomial Tree Sequence #2</li>
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

public class SingleFactorStateEvolver
	implements PointStateEvolver
{
	private double _a = Double.NaN;
	private double _sigma = Double.NaN;
	private FundingLabel _fundingLabel = null;
	private R1ToR1 _initialSingleFactorFunction = null;
	private UnivariateSequenceGenerator _univariateSequenceGenerator = null;

	/**
	 * <i>SingleFactorStateEvolver</i> Constructor
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param sigma Sigma
	 * @param a A
	 * @param initialSingleFactorFunction The Initial Instantaneous Forward Rate Term Structure
	 * @param univariateSequenceGenerator Univariate Random Sequence Generator
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public SingleFactorStateEvolver (
		final FundingLabel fundingLabel,
		final double sigma,
		final double a,
		final R1ToR1 initialSingleFactorFunction,
		final UnivariateSequenceGenerator univariateSequenceGenerator)
		throws Exception
	{
		if (null == (_fundingLabel = fundingLabel) ||
			!NumberUtil.IsValid (_sigma = sigma) ||
			!NumberUtil.IsValid (_a = a) ||
			null == (_initialSingleFactorFunction = initialSingleFactorFunction) ||
			null == (_univariateSequenceGenerator = univariateSequenceGenerator))
		{
			throw new Exception ("SingleFactorStateEvolver Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Funding Label
	 * 
	 * @return The Funding Label
	 */

	public FundingLabel fundingLabel()
	{
		return _fundingLabel;
	}

	/**
	 * Retrieve Sigma
	 * 
	 * @return Sigma
	 */

	public double sigma()
	{
		return _sigma;
	}

	/**
	 * Retrieve A
	 * 
	 * @return A
	 */

	public double a()
	{
		return _a;
	}

	/**
	 * Retrieve the Initial Instantaneous Forward Rate Term Structure
	 * 
	 * @return The Initial Instantaneous Forward Rate Term Structure
	 */

	public R1ToR1 initialSingleFactorFunction()
	{
		return _initialSingleFactorFunction;
	}

	/**
	 * Retrieve the Random Sequence Generator
	 * 
	 * @return The Random Sequence Generator
	 */

	public UnivariateSequenceGenerator univariateSequenceGenerator()
	{
		return _univariateSequenceGenerator;
	}

	/**
	 * Calculate the Alpha
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * 
	 * @return Alpha
	 * 
	 * @throws Exception Thrown if Alpha cannot be computed
	 */

	public double alpha (
		final int spotDate,
		final int viewDate)
		throws Exception
	{
		if (spotDate > viewDate) {
			throw new Exception ("SingleFactorStateEvolver::alpha => Invalid Inputs");
		}

		double alphaVol = _sigma * (1. - Math.exp (_a * (viewDate - spotDate) / 365.25)) / _a;

		return _initialSingleFactorFunction.evaluate (viewDate) + 0.5 * alphaVol * alphaVol;
	}

	/**
	 * Calculate the Theta
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * 
	 * @return Theta
	 * 
	 * @throws Exception Thrown if Theta cannot be computed
	 */

	public double theta (
		final int spotDate,
		final int viewDate)
		throws Exception
	{
		if (spotDate > viewDate) {
			throw new Exception ("SingleFactorStateEvolver::theta => Invalid Inputs");
		}

		return _initialSingleFactorFunction.derivative (viewDate, 1) +
			_a * _initialSingleFactorFunction.evaluate (viewDate) +
			_sigma * _sigma / (2. * _a) * (1. - Math.exp (-2. * _a * (viewDate - spotDate) / 365.25));
	}

	/**
	 * Calculate the Short Rate Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param shortRate The Short Rate
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Short Rate Increment
	 * 
	 * @throws Exception Thrown if the Short Rate cannot be computed
	 */

	public double shortRateIncrement (
		final int spotDate,
		final int viewDate,
		final double shortRate,
		final int viewTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || !NumberUtil.IsValid (shortRate)) {
			throw new Exception ("SingleFactorStateEvolver::shortRateIncrement => Invalid Inputs");
		}

		double annualizedIncrement = 1. * viewTimeIncrement / 365.25;

		return (theta (spotDate, viewDate) - _a * shortRate) * annualizedIncrement +
			_sigma * Math.sqrt (annualizedIncrement) * _univariateSequenceGenerator.random();
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
			null == previousLSQMPointUpdate || !(previousLSQMPointUpdate instanceof ShortRateUpdate))
		{
			return null;
		}

		int date = spotDate;
		int timeIncrement = 1;
		double initialShortRate = Double.NaN;
		int finalDate = spotDate + spotTimeIncrement;

		try {
			initialShortRate = ((ShortRateUpdate) previousLSQMPointUpdate).realizedFinalShortRate();
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		double shortRate = initialShortRate;

		while (date < finalDate) {
			try {
				shortRate += shortRateIncrement (spotDate, date, shortRate, timeIncrement);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			++date;
		}

		double adf = Math.exp (-1. * _a * spotTimeIncrement);

		double b = (1. - adf) / _a;

		try {
			return ShortRateUpdate.Create (
				_fundingLabel,
				spotDate,
				finalDate,
				viewDate,
				initialShortRate,
				shortRate,
				initialShortRate * adf + alpha (spotDate, finalDate) - alpha (spotDate, viewDate) * adf,
				0.5 * _sigma * _sigma * (1. - adf * adf) / _a,
				Math.exp (
					b * _initialSingleFactorFunction.evaluate (viewDate) -
						0.25 * _sigma * _sigma *
							(1. - Math.exp (-2. * _a * (viewDate - spotDate) / 365.25)) * b * b / _a
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Metrics associated with the Transition that results from using a Trinomial Tree Using the
	 *  Starting Node Metrics
	 * 
	 * @param spotDate The Spot/Epoch Date
	 * @param initialDate The Initial Date
	 * @param terminalDate The Terminal Date
	 * @param initialTrinomialTreeNodeMetrics The Initial Node Metrics
	 * 
	 * @return The Hull White Transition Metrics
	 */

	public TrinomialTreeTransitionMetrics evolveTrinomialTree (
		final int spotDate,
		final int initialDate,
		final int terminalDate,
		final TrinomialTreeNodeMetrics initialTrinomialTreeNodeMetrics)
	{
		if (initialDate < spotDate || terminalDate <= initialDate) {
			return null;
		}

		long treeTimeIndex = 0L;
		double expectedTerminalX = 0.;
		long treeStochasticBaseIndex = 0L;

		if (null != initialTrinomialTreeNodeMetrics) {
			expectedTerminalX = initialTrinomialTreeNodeMetrics.x();

			treeTimeIndex = initialTrinomialTreeNodeMetrics.timeIndex() + 1;

			treeStochasticBaseIndex = initialTrinomialTreeNodeMetrics.xStochasticIndex();
		}

		double adf = Math.exp (-1. * _a * (terminalDate - initialDate) / 365.25);

		try {
			return new TrinomialTreeTransitionMetrics (
				initialDate,
				terminalDate,
				treeTimeIndex,
				treeStochasticBaseIndex,
				expectedTerminalX * adf,
				0.5 * _sigma * _sigma * (1. - adf * adf) / _a,
				alpha (spotDate, terminalDate)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Evolve the Trinomial Tree Sequence #1
	 * 
	 * @param spotDate The Spot Date
	 * @param initialDate The Initial Date
	 * @param dayIncrement The Day Increment
	 * @param incrementCount Number of Times to Increment
	 * @param trinomialTreeNodeMetrics Starting Node Metrics
	 * @param trinomialTreeSequenceMetrics The Sequence Metrics
	 * 
	 * @return TRUE - The Tree Successfully Evolved
	 */

	public boolean evolveTrinomialTreeSequence (
		final int spotDate,
		final int initialDate,
		final int dayIncrement,
		final int incrementCount,
		final TrinomialTreeNodeMetrics trinomialTreeNodeMetrics,
		final TrinomialTreeSequenceMetrics trinomialTreeSequenceMetrics)
	{
		if (initialDate < spotDate || 0 >= dayIncrement || null == trinomialTreeSequenceMetrics) {
			return false;
		}

		if (0 == incrementCount) {
			return true;
		}

		TrinomialTreeTransitionMetrics trinomialTreeTransitionMetrics = evolveTrinomialTree (
			spotDate,
			initialDate,
			initialDate + dayIncrement,
			trinomialTreeNodeMetrics
		);

		if (!trinomialTreeSequenceMetrics.addTransitionMetrics (trinomialTreeTransitionMetrics)) {
			return false;
		}

		TrinomialTreeNodeMetrics upTrinomialTreeNodeMetrics = trinomialTreeTransitionMetrics.upNodeMetrics();

		if (!trinomialTreeSequenceMetrics.addNodeMetrics (upTrinomialTreeNodeMetrics) || (
				null != trinomialTreeNodeMetrics &&
				!trinomialTreeSequenceMetrics.setTransitionProbability (
					trinomialTreeNodeMetrics,
					upTrinomialTreeNodeMetrics,
					trinomialTreeTransitionMetrics.probabilityUp()
				)
			) || !evolveTrinomialTreeSequence (
				spotDate,
				initialDate + dayIncrement,
				dayIncrement,
				incrementCount - 1,
				upTrinomialTreeNodeMetrics,
				trinomialTreeSequenceMetrics
			)
		)
		{
			return false;
		}

		TrinomialTreeNodeMetrics downTrinomialTreeNodeMetrics =
			trinomialTreeTransitionMetrics.downNodeMetrics();

		if (!trinomialTreeSequenceMetrics.addNodeMetrics (downTrinomialTreeNodeMetrics) || (
				null != trinomialTreeNodeMetrics &&
				!trinomialTreeSequenceMetrics.setTransitionProbability (
					trinomialTreeNodeMetrics,
					downTrinomialTreeNodeMetrics,
					trinomialTreeTransitionMetrics.probabilityDown()
				)
			) || !evolveTrinomialTreeSequence (
				spotDate,
				initialDate + dayIncrement,
				dayIncrement,
				incrementCount - 1,
				downTrinomialTreeNodeMetrics,
				trinomialTreeSequenceMetrics
			)
		)
		{
			return false;
		}

		TrinomialTreeNodeMetrics stayTrinomialTreeNodeMetrics =
			trinomialTreeTransitionMetrics.stayNodeMetrics();

		if (!trinomialTreeSequenceMetrics.addNodeMetrics (stayTrinomialTreeNodeMetrics) || (
				null != trinomialTreeNodeMetrics &&
				!trinomialTreeSequenceMetrics.setTransitionProbability (
					trinomialTreeNodeMetrics,
					stayTrinomialTreeNodeMetrics,
					trinomialTreeTransitionMetrics.probabilityStay()
				)
			) || !evolveTrinomialTreeSequence (
				spotDate,
				initialDate + dayIncrement,
				dayIncrement,
				incrementCount - 1,
				stayTrinomialTreeNodeMetrics,
				trinomialTreeSequenceMetrics
			)
		)
		{
			return false;
		}

		return true;
	}

	/**
	 * Evolve the Trinomial Tree Sequence
	 * 
	 * @param spotDate The Spot Date
	 * @param dayIncrement The Day Increment
	 * @param incrementCount Number of Times to Increment
	 * 
	 * @return The Sequence Metrics
	 */

	public TrinomialTreeSequenceMetrics evolveTrinomialTreeSequence (
		final int spotDate,
		final int dayIncrement,
		final int incrementCount)
	{
		TrinomialTreeSequenceMetrics trinomialTreeSequenceMetrics = new TrinomialTreeSequenceMetrics();

		return evolveTrinomialTreeSequence (
			spotDate,
			spotDate,
			dayIncrement,
			incrementCount,
			null,
			trinomialTreeSequenceMetrics
		) ? trinomialTreeSequenceMetrics : null;
	}
}
