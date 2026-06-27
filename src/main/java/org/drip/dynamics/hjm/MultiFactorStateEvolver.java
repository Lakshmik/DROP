
package org.drip.dynamics.hjm;

import org.drip.analytics.date.JulianDate;
import org.drip.dynamics.evolution.LSQMPointUpdate;
import org.drip.dynamics.evolution.PointStateEvolver;
import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.sequence.random.PrincipalFactorSequenceGenerator;
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
 * <i>MultiFactorStateEvolver</i> sets up and implements the Base Multi-Factor No-arbitrage Dynamics of the
 * 	Rates State Quantifiers as formulated in:
 * 
 * <ul>
 * 	<li>
 * 		Heath, D., R. Jarrow, and A. Morton (1992): Bond Pricing and Term Structure of Interest Rates: A New
 * 			Methodology for Contingent Claims Valuation <i>Econometrica</i> <b>60 (1)</b> 77-105
 * 	</li>
 * </ul>
 *
 * 	In particular it looks to evolve the Multi-factor Instantaneous Forward Rates. It provides the following
 * 	Functions:
 *
 *  <ul>
 * 		<li><i>MultiFactorStateEvolver</i> Constructor</li>
 * 		<li>Retrieve the Funding Label</li>
 * 		<li>Retrieve the Forward Label</li>
 * 		<li>Retrieve the Multi-factor Volatility Instance</li>
 * 		<li>Retrieve the Initial Instantaneous Forward Rate Term Structure</li>
 * 		<li>Compute the Instantaneous Forward Rate Increment given the View Date, the Target Date, and the View Time Increment</li>
 * 		<li>Compute the Proportional Price Increment given the View Date, the Target Date, the Short Rate, and the View Time Increment</li>
 * 		<li>Compute the Short Rate Increment given the Spot Date, the View Date, and the View Time Increment</li>
 * 		<li>Compute the Continuously Compounded Short Rate Increment given the Spot Date, the View Date, the Target Date, the Continuously Compounded Short Rate, the Current Short Rate, and the View Time Increment</li>
 * 		<li>Compute the LIBOR Forward Rate Increment given the Spot Date, the View Date, the Target Date, the Current LIBOR Forward Rate, and the View Time Increment</li>
 * 		<li>Compute the Shifted LIBOR Forward Rate Increment given the Spot Date, the View Date, the Target Date, the Current Shifted LIBOR Forward Rate, and the View Time Increment</li>
 * 		<li>Evolve the Latent State and return the LSQM Point Update</li>
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

public class MultiFactorStateEvolver
	implements PointStateEvolver
{
	private ForwardLabel _forwardLabel = null;
	private FundingLabel _fundingLabel = null;
	private MultiFactorVolatility _multiFactorVolatility = null;
	private R1ToR1 _initialInstantaneousForwardRateFunction = null;

	/**
	 * <i>MultiFactorStateEvolver</i> Constructor
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param forwardLabel The Forward Latent State Label
	 * @param multiFactorVolatility The Multi-Factor Volatility Instance
	 * @param initialInstantaneousForwardRateFunction The Initial Instantaneous Forward Rate Term Structure
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public MultiFactorStateEvolver (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final MultiFactorVolatility multiFactorVolatility,
		final R1ToR1 initialInstantaneousForwardRateFunction)
		throws Exception
	{
		if (null == (_fundingLabel = fundingLabel) ||
			null == (_forwardLabel = forwardLabel) ||
			null == (_multiFactorVolatility = multiFactorVolatility) ||
			null == (_initialInstantaneousForwardRateFunction = initialInstantaneousForwardRateFunction))
		{
			throw new Exception ("MultiFactorStateEvolver Constructor => Invalid Inputs");
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
	 * Retrieve the Forward Label
	 * 
	 * @return The Forward Label
	 */

	public ForwardLabel forwardLabel()
	{
		return _forwardLabel;
	}

	/**
	 * Retrieve the Multi-factor Volatility Instance
	 * 
	 * @return The Multi-factor Volatility Instance
	 */

	public MultiFactorVolatility multiFactorVolatility()
	{
		return _multiFactorVolatility;
	}

	/**
	 * Retrieve the Initial Instantaneous Forward Rate Term Structure
	 * 
	 * @return The Initial Instantaneous Forward Rate Term Structure
	 */

	public R1ToR1 instantaneousForwardInitialTermStructure()
	{
		return _initialInstantaneousForwardRateFunction;
	}

	/**
	 * Compute the Instantaneous Forward Rate Increment given the View Date, the Target Date, and the View
	 * 	Time Increment
	 * 
	 * @param viewDate The View Date
	 * @param targetDate The Target Date
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Instantaneous Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Instantaneous Forward Rate Increment cannot be computed
	 */

	public double instantaneousForwardRateIncrement (
		final int viewDate,
		final int targetDate,
		final int viewTimeIncrement)
		throws Exception
	{
		if (targetDate <= viewDate) {
			throw new Exception (
				"MultiFactorStateEvolver::instantaneousForwardRateIncrement => Invalid Inputs"
			);
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double instantaneousForwardRateIncrement = 0.;
		double annualizedTimeIncrement = 1. * viewTimeIncrement / 365.25;

		double annualizedTimeIncrementSQRT = Math.sqrt (annualizedTimeIncrement);

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			double weightedFactorPointVolatility = _multiFactorVolatility.weightedFactorPointVolatility (
				factorIndex,
				viewDate,
				targetDate
			);

			if (!NumberUtil.IsValid (weightedFactorPointVolatility)) {
				throw new Exception (
					"MultiFactorStateEvolver::instantaneousForwardRateIncrement => Cannot compute View/Target Date Point Volatility"
				);
			}

			instantaneousForwardRateIncrement += _multiFactorVolatility.volatilityIntegral (
				factorIndex,
				viewDate,
				targetDate
			) * weightedFactorPointVolatility * annualizedTimeIncrement +
			weightedFactorPointVolatility * annualizedTimeIncrementSQRT *
				multivariateRandomArray[factorIndex];
		}

		return instantaneousForwardRateIncrement;
	}

	/**
	 * Compute the Proportional Price Increment given the View Date, the Target Date, the Short Rate, and the
	 *  View Time Increment
	 * 
	 * @param viewDate The View Date
	 * @param targetDate The Target Date
	 * @param shortRate The Short Rate
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Proportional Price Increment
	 * 
	 * @throws Exception Thrown if the Proportional Price Increment cannot be computed
	 */

	public double proportionalPriceIncrement (
		final int viewDate,
		final int targetDate,
		final double shortRate,
		final int viewTimeIncrement)
		throws Exception
	{
		if (targetDate <= viewDate || !NumberUtil.IsValid (shortRate)) {
			throw new Exception ("MultiFactorStateEvolver::proportionalPriceIncrement => Invalid Inputs");
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double annualizedTimeIncrement = 1. * viewTimeIncrement / 365.25;
		double proportionalPriceIncrement = shortRate * annualizedTimeIncrement;

		double annualizedTimeIncrementSQRT = Math.sqrt (annualizedTimeIncrement);

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			proportionalPriceIncrement -=
				_multiFactorVolatility.volatilityIntegral (factorIndex, viewDate, targetDate) *
				annualizedTimeIncrementSQRT * multivariateRandomArray[factorIndex];
		}

		return proportionalPriceIncrement;
	}

	/**
	 * Compute the Short Rate Increment given the Spot Date, the View Date, and the View Time Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Short Rate Increment
	 * 
	 * @throws Exception Thrown if the Short Rate Increment cannot be computed
	 */

	public double shortRateIncrement (
		final int spotDate,
		final int viewDate,
		final int viewTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate) {
			throw new Exception ("MultiFactorStateEvolver::shortRateIncrement => Invalid Inputs");
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double shortRateIncrement = 0.;
		double annualizedIncrement = 1. * viewTimeIncrement / 365.25;

		double annualizedIncrementSQRT = Math.sqrt (annualizedIncrement);

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			double viewWeightedFactorVolatility = _multiFactorVolatility.weightedFactorPointVolatility (
				factorIndex,
				viewDate,
				viewDate
			);

			if (!NumberUtil.IsValid (viewWeightedFactorVolatility)) {
				throw new Exception (
					"MultiFactorStateEvolver::shortRateIncrement => Cannot compute View Date Factor Volatility"
				);
			}

			shortRateIncrement += _multiFactorVolatility.volatilityIntegral (factorIndex, spotDate, viewDate)
				* viewWeightedFactorVolatility * annualizedIncrement + viewWeightedFactorVolatility *
				annualizedIncrementSQRT * multivariateRandomArray[factorIndex];
		}

		return shortRateIncrement;
	}

	/**
	 * Compute the Continuously Compounded Short Rate Increment given the Spot Date, the View Date, the
	 *  Target Date, the Continuously Compounded Short Rate, the Current Short Rate, and the View Time
	 *  Increment.
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param targetDate The Target Date
	 * @param compoundedShortRate The Compounded Short Rate
	 * @param shortRate The Short Rate
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Short Rate Increment
	 * 
	 * @throws Exception Thrown if the Continuously Compounded Short Rate Increment cannot be computed
	 */

	public double compoundedShortRateIncrement (
		final int spotDate,
		final int viewDate,
		final int targetDate,
		final double compoundedShortRate,
		final double shortRate,
		final int viewTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || viewDate >= targetDate) {
			throw new Exception ("MultiFactorStateEvolver::compoundedShortRateIncrement => Invalid Inputs");
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double annualizedIncrement = 1. * viewTimeIncrement / 365.25;
		double compoundedShortRateIncrement = (compoundedShortRate - shortRate) * annualizedIncrement;

		double annualizedIncrementSQRT = Math.sqrt (annualizedIncrement);

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			double viewTargetVolatilityIntegral = _multiFactorVolatility.volatilityIntegral (
				factorIndex,
				viewDate,
				targetDate
			);

			compoundedShortRateIncrement += 0.5 * viewTargetVolatilityIntegral *
				viewTargetVolatilityIntegral * annualizedIncrement + viewTargetVolatilityIntegral *
				annualizedIncrementSQRT * multivariateRandomArray[factorIndex];
		}

		return compoundedShortRateIncrement * 365.25 / (targetDate - viewDate);
	}

	/**
	 * Compute the LIBOR Forward Rate Increment given the Spot Date, the View Date, the Target Date, the
	 *  Current LIBOR Forward Rate, and the View Time Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param targetDate The Target Date
	 * @param liborForward The LIBOR Forward Rate
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the LIBOR Forward Rate Increment cannot be computed
	 */

	public double liborForwardRateIncrement (
		final int spotDate,
		final int viewDate,
		final int targetDate,
		final double liborForward,
		final int viewTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || viewDate >= targetDate || !NumberUtil.IsValid (liborForward)) {
			throw new Exception ("MultiFactorStateEvolver::liborForwardRateIncrement => Invalid Inputs");
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double annualizedTimeIncrementSQRT = Math.sqrt (1. * viewTimeIncrement / 365.25);

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double liborForwardVolatilityIncrement = 0.;

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			liborForwardVolatilityIncrement +=
				_multiFactorVolatility.volatilityIntegral (factorIndex, viewDate, targetDate) * (
					_multiFactorVolatility.volatilityIntegral (factorIndex, spotDate, targetDate) +
					annualizedTimeIncrementSQRT * multivariateRandomArray[factorIndex]
				);
		}

		return (liborForward + (365.25 / (targetDate - viewDate))) * liborForwardVolatilityIncrement;
	}

	/**
	 * Compute the Shifted LIBOR Forward Rate Increment given the Spot Date, the View Date, the Target Date,
	 * 	the Current Shifted LIBOR Forward Rate, and the View Time Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param targetDate The Target Date
	 * @param shiftedLIBORForward The Shifted LIBOR Forward Rate
	 * @param viewTimeIncrement The View Time Increment
	 * 
	 * @return The Shifted Forward Rate Increment
	 * 
	 * @throws Exception Thrown if the Shifted LIBOR Forward Rate Increment cannot be computed
	 */

	public double shiftedLIBORForwardIncrement (
		final int spotDate,
		final int viewDate,
		final int targetDate,
		final double shiftedLIBORForward,
		final int viewTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || viewDate >= targetDate || !NumberUtil.IsValid (shiftedLIBORForward)) {
			throw new Exception ("MultiFactorStateEvolver::shiftedLIBORForwardIncrement => Invalid Inputs");
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double annualizedTimeIncrementSQRT = Math.sqrt (1. * viewTimeIncrement / 365.25);

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double shiftedLIBORVolatilityIncrement = 0.;

		for (int factorIndex = 0; factorIndex < principalFactorSequenceGenerator.numFactor(); ++factorIndex)
		{
			shiftedLIBORVolatilityIncrement +=
				_multiFactorVolatility.volatilityIntegral (factorIndex, viewDate, targetDate) * (
					_multiFactorVolatility.volatilityIntegral (factorIndex, spotDate, targetDate) +
					annualizedTimeIncrementSQRT * multivariateRandomArray[factorIndex]
				);
		}

		return shiftedLIBORForward * shiftedLIBORVolatilityIncrement;
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

	@Override public org.drip.dynamics.evolution.LSQMPointUpdate evolve (
		final int spotDate,
		final int viewDate,
		final int spotTimeIncrement,
		final LSQMPointUpdate previousLSQMPointUpdate)
	{
		if (spotDate > viewDate ||
			null == previousLSQMPointUpdate || !(previousLSQMPointUpdate instanceof ShortForwardRateUpdate))
		{
			return null;
		}

		PrincipalFactorSequenceGenerator principalFactorSequenceGenerator =
			_multiFactorVolatility.principalFactorSequenceGenerator();

		double[] multivariateRandomArray = principalFactorSequenceGenerator.random();

		double annualizedIncrement = 1. * spotTimeIncrement / 365.25;
		ShortForwardRateUpdate initialShortForwardRateUpdate =
			(ShortForwardRateUpdate) previousLSQMPointUpdate;

		double annualizedIncrementSQRT = Math.sqrt (annualizedIncrement);

		try {
			double initialPrice = initialShortForwardRateUpdate.price();

			double initialShortRate = initialShortForwardRateUpdate.shortRate();

			double initialLIBORForward = initialShortForwardRateUpdate.liborForwardRate();

			int targetDate = new JulianDate (viewDate).addTenor (_forwardLabel.tenor()).julian();

			double initialCompoundedShortRate = initialShortForwardRateUpdate.compoundedShortRate();

			double shortRateIncrement = 0.;
			double shiftedLIBORForwardIncrement = 0.;
			double instantaneousForwardIncrement = 0.;
			double priceIncrement = initialShortRate * annualizedIncrement;
			double compoundedShortRateIncrement = (initialCompoundedShortRate - initialShortRate) *
				annualizedIncrement;

			for (int factorIndex = 0;
				factorIndex < principalFactorSequenceGenerator.numFactor();
				++factorIndex)
			{
				double viewDateFactorVolatility = _multiFactorVolatility.weightedFactorPointVolatility (
					factorIndex,
					viewDate,
					viewDate
				);

				if (!NumberUtil.IsValid (viewDateFactorVolatility)) {
					return null;
				}

				double viewTargetFactorVolatility = _multiFactorVolatility.weightedFactorPointVolatility (
					factorIndex,
					viewDate,
					targetDate
				);

				if (!NumberUtil.IsValid (viewTargetFactorVolatility)) {
					return null;
				}

				double viewTargetVolatilityIntegral = _multiFactorVolatility.volatilityIntegral (
					factorIndex,
					viewDate,
					targetDate
				);

				if (!NumberUtil.IsValid (viewTargetVolatilityIntegral)) {
					return null;
				}

				double spotViewVolatilityIntegral = _multiFactorVolatility.volatilityIntegral (
					factorIndex,
					spotDate,
					viewDate
				);

				if (!NumberUtil.IsValid (spotViewVolatilityIntegral)) {
					return null;
				}

				double spotTargetVolatilityIntegral = _multiFactorVolatility.volatilityIntegral (
					factorIndex,
					spotDate,
					targetDate
				);

				if (!NumberUtil.IsValid (spotTargetVolatilityIntegral)) {
					return null;
				}

				double scaledMultivariateRandom =
					annualizedIncrementSQRT * multivariateRandomArray[factorIndex];
				instantaneousForwardIncrement +=
					viewTargetVolatilityIntegral * viewTargetFactorVolatility * annualizedIncrement +
					viewTargetFactorVolatility * scaledMultivariateRandom;
				shortRateIncrement +=
					spotViewVolatilityIntegral * viewDateFactorVolatility * annualizedIncrement +
					viewDateFactorVolatility * scaledMultivariateRandom;
				compoundedShortRateIncrement +=
					0.5 * viewTargetVolatilityIntegral * viewTargetVolatilityIntegral * annualizedIncrement +
					viewTargetVolatilityIntegral * scaledMultivariateRandom;
				shiftedLIBORForwardIncrement += viewTargetVolatilityIntegral *
					(spotTargetVolatilityIntegral + scaledMultivariateRandom);
				priceIncrement -= viewTargetVolatilityIntegral * scaledMultivariateRandom;
			}

			priceIncrement *= initialPrice;
			compoundedShortRateIncrement *= 365.25 / (targetDate - viewDate);
			double liborForwardIncrement = (initialLIBORForward + (365.25 / (targetDate - viewDate))) *
				shiftedLIBORForwardIncrement;

			return ShortForwardRateUpdate.Create (
				_fundingLabel,
				_forwardLabel,
				spotDate,
				spotDate + spotTimeIncrement,
				targetDate,
				initialShortForwardRateUpdate.instantaneousForwardRate() +
				instantaneousForwardIncrement,
				instantaneousForwardIncrement,
				initialLIBORForward + liborForwardIncrement,
				liborForwardIncrement,
				initialShortForwardRateUpdate.shiftedLIBORForwardRate() + shiftedLIBORForwardIncrement,
				shiftedLIBORForwardIncrement,
				initialShortRate + shortRateIncrement,
				shortRateIncrement,
				initialCompoundedShortRate + compoundedShortRateIncrement,
				compoundedShortRateIncrement,
				initialPrice + priceIncrement,
				priceIncrement
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
