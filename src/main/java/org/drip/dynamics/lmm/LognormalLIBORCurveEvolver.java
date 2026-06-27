
package org.drip.dynamics.lmm;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.Helper;
import org.drip.dynamics.evolution.CurveStateEvolver;
import org.drip.dynamics.evolution.LSQMCurveUpdate;
import org.drip.function.definition.R1ToR1;
import org.drip.spline.grid.OverlappingStretchSpan;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.curve.BasisSplineForwardRate;
import org.drip.state.curve.DiscountFactorDiscountCurve;
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
 * <i>LognormalLIBORCurveEvolver</i> sets up and implements the Multi-Factor No-arbitrage Dynamics of the
 * 	full Curve Rates State Quantifiers traced from the Evolution of the LIBOR Forward Rate as formulated in:
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
 * 		<li>Create a <i>LognormalLIBORCurveEvolver</i> Instance</li>
 * 		<li><i>LognormalLIBORCurveEvolver</i> Constructor</li>
 * 		<li>Retrieve the Funding Label</li>
 * 		<li>Retrieve the Forward Label</li>
 * 		<li>Retrieve the Number of Forward Tenors comprising the Span Tenor</li>
 * 		<li>Retrieve the LIBOR Curve Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Discount Factor Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the LIBOR Increment Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Discount Factor Increment Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Instantaneous Continuously Compounded Forward Rate Increment Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Spot Rate Increment Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Instantaneous Effective Annual Forward Rate Increment Segment Custom Builder Control Instance</li>
 * 		<li>Retrieve the Instantaneous Nominal Annual Forward Rate Increment Segment Custom Builder Control Instance</li>
 * 		<li>Evolve the Latent State and return the LSQM Curve Update</li>
 * 		<li>Simulate the Principal Metric from the Start to the End Date</li>
 * 		<li>Construct an Array of Forward Curves that Result from the Simulation</li>
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

public class LognormalLIBORCurveEvolver
	implements CurveStateEvolver
{
	private int _forwardTenorCount = -1;
	private ForwardLabel _forwardLabel = null;
	private FundingLabel _fundingLabel = null;
	private SegmentCustomBuilderControl[] _liborSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[] _discountFactorSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[] _liborIncrementSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[] _spotRateIncrementSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[] _discountFactorIncrementSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[] _continuousForwardIncrementSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[]
		_instantaneousNominalForwardSegmentCustomBuilderControlArray = null;
	private SegmentCustomBuilderControl[]
		_instantaneousEffectiveForwardSegmentCustomBuilderControlArray = null;

	/**
	 * Create a <i>LognormalLIBORCurveEvolver</i> Instance
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param forwardLabel The Forward Latent State Label
	 * @param forwardTenorCount Number of Forward Tenors to Build the Span
	 * @param segmentCustomBuilderControl The Common Span Segment Custom Builder Control Instance
	 * 
	 * @return The <i>LognormalLIBORCurveEvolver</i> Instance
	 */

	public static final LognormalLIBORCurveEvolver Create (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int forwardTenorCount,
		final SegmentCustomBuilderControl segmentCustomBuilderControl)
	{
		try {
			return new LognormalLIBORCurveEvolver (
				fundingLabel,
				forwardLabel,
				forwardTenorCount,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl,
				segmentCustomBuilderControl
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private double forwardDerivative (
		final ForwardCurve forwardCurve,
		final int targetPointDate)
		throws Exception
	{
		return new R1ToR1 (null) {
			@Override public double evaluate (
				final double date)
				throws Exception
			{
				return forwardCurve.forward ((int) date);
			}
		}.derivative (targetPointDate, 1);
	}

	private double continuousForwardRateIncrement (
		final int viewDate,
		final double annualizedIncrement,
		final double annualizedIncrementSQRT,
		final ForwardCurve forwardCurve,
		final double[] multivariateRandomArray,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
		throws Exception
	{
		return new R1ToR1 (null) {
			@Override public double evaluate (
				final double date)
				throws Exception
			{
				double forwardPointVolatilityModulus = 0.;
				double pointVolatilityMultifactorRandom = 0.;

				double[] continuousForwardVolatilityArray =
					lognormalLIBORVolatility.continuousForwardVolatility (
						(int) date,
						forwardCurve
					);

				if (null != continuousForwardVolatilityArray) {
					for (int multivariateRandomIndex = 0;
						multivariateRandomIndex < multivariateRandomArray.length;
						++multivariateRandomIndex)
					{
						forwardPointVolatilityModulus +=
							continuousForwardVolatilityArray[multivariateRandomIndex] *
							continuousForwardVolatilityArray[multivariateRandomIndex];
						pointVolatilityMultifactorRandom +=
							continuousForwardVolatilityArray[multivariateRandomIndex] *
							multivariateRandomArray[multivariateRandomIndex];
					}
				}

				return (forwardCurve.forward ((int) date) + 0.5 * forwardPointVolatilityModulus) *
					annualizedIncrement + pointVolatilityMultifactorRandom * annualizedIncrementSQRT;
			}
		}.derivative (viewDate, 1);
	}

	private double spotRateIncrement (
		final int viewDate,
		final double annualizedIncrement,
		final double annualizedIncrementSQRT,
		final MergedDiscountForwardCurve discountCurve,
		final double[] multivariateRandomArray,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
		throws Exception
	{
		return new R1ToR1 (null) {
			@Override public double evaluate (
				final double date)
				throws Exception
			{
				int dateInteger = (int) date;
				double pointVolatilityMultifactorRandom = 0.;

				double[] continuousForwardVolatilityArray =
					lognormalLIBORVolatility.continuousForwardVolatility (dateInteger, discountCurve);

				if (null != continuousForwardVolatilityArray) {
					for (int multivariateRandomIndex = 0;
						multivariateRandomIndex < multivariateRandomArray.length;
						++multivariateRandomIndex)
					{
						pointVolatilityMultifactorRandom +=
							continuousForwardVolatilityArray[multivariateRandomIndex] *
							multivariateRandomArray[multivariateRandomIndex];
					}
				}

				return discountCurve.forward (dateInteger, dateInteger + 1) * annualizedIncrement +
					pointVolatilityMultifactorRandom * annualizedIncrementSQRT;
			}
		}.derivative (viewDate, 1);
	}

	private BGMForwardTenorSnap timeSnap (
		final int spotDate,
		final int targetPointDate,
		final double annualizedIncrement,
		final double annualizedIncrementSQRT,
		final String forwardTenor,
		final ForwardCurve forwardCurve,
		final MergedDiscountForwardCurve discountCurve,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
	{
		double[] lognormalFactorPointVolatilityArray =
			lognormalLIBORVolatility.factorPointVolatility (spotDate, targetPointDate);

		double[] continuousForwardVolatilityArray =
			lognormalLIBORVolatility.continuousForwardVolatility (targetPointDate, forwardCurve);

		double[] multivariateRandomArray =
			lognormalLIBORVolatility.principalFactorSequenceGenerator().random();

		double crossVolatilityDotProduct = 0.;
		double lognormalPointVolatilityModulus = 0.;
		double liborVolatilityMultiFactorRandom = 0.;
		double continuousForwardVolatilityModulus = 0.;
		double forwardVolatilityMultiFactorRandom = 0.;

		for (int lognormalFactorPointVolatilityIndex = 0;
			lognormalFactorPointVolatilityIndex < lognormalFactorPointVolatilityArray.length;
			++lognormalFactorPointVolatilityIndex)
		{
			lognormalPointVolatilityModulus +=
				lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
				lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex];
			crossVolatilityDotProduct +=
				lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
				continuousForwardVolatilityArray[lognormalFactorPointVolatilityIndex];
			liborVolatilityMultiFactorRandom +=
				lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
				multivariateRandomArray[lognormalFactorPointVolatilityIndex] * annualizedIncrementSQRT;
			continuousForwardVolatilityModulus +=
				continuousForwardVolatilityArray[lognormalFactorPointVolatilityIndex] *
				continuousForwardVolatilityArray[lognormalFactorPointVolatilityIndex];
			forwardVolatilityMultiFactorRandom +=
				continuousForwardVolatilityArray[lognormalFactorPointVolatilityIndex] *
				multivariateRandomArray[lognormalFactorPointVolatilityIndex] * annualizedIncrementSQRT;
		}

		try {
			double libor = forwardCurve.forward (targetPointDate);

			double discountFactor = discountCurve.df (targetPointDate);

			double spotRate = discountCurve.forward (spotDate, spotDate + 1);

			double continuousForwardRate = forwardCurve.forward (targetPointDate);

			double dcf = Helper.TenorToYearFraction (forwardTenor);

			double liborDCF = dcf * libor;

			double liborIncrement = annualizedIncrement * (forwardDerivative (forwardCurve, targetPointDate) +
				libor * crossVolatilityDotProduct +
				(lognormalPointVolatilityModulus * libor * liborDCF / (1. + liborDCF))) +
				libor * liborVolatilityMultiFactorRandom;

			double discountFactorIncrement =
				discountFactor * (spotRate - continuousForwardRate) * annualizedIncrement -
				forwardVolatilityMultiFactorRandom;

			double continuousForwardRateIncrement = continuousForwardRateIncrement (
				targetPointDate,
				annualizedIncrement,
				annualizedIncrementSQRT,
				forwardCurve,
				multivariateRandomArray,
				lognormalLIBORVolatility
			);

			double continuousForwardRateEvolved = continuousForwardRate + continuousForwardRateIncrement;

			return new BGMForwardTenorSnap (
				targetPointDate,
				libor + liborIncrement,
				liborIncrement,
				discountFactor + discountFactorIncrement,
				discountFactorIncrement,
				continuousForwardRateIncrement,
				spotRateIncrement (
					targetPointDate,
					annualizedIncrement,
					annualizedIncrementSQRT,
					discountCurve,
					multivariateRandomArray,
					lognormalLIBORVolatility
				),
				Math.exp (continuousForwardRateEvolved) - 1.,
				(Math.exp (dcf * continuousForwardRateEvolved) - 1.) / dcf,
				Math.sqrt (lognormalPointVolatilityModulus),
				Math.sqrt (continuousForwardVolatilityModulus)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private PathwiseQMRealization simulateLIBOR (
		final int evolutionDate,
		final int viewDate,
		final double annualizedIncrement,
		final double annualizedIncrementSQRT,
		final ForwardCurve forwardCurve,
		final String forwardTenor,
		final double forwardDCF,
		final LognormalLIBORVolatility lognormalLIBORVolatility)
	{
		int[] tenorDateArray = new int[_forwardTenorCount + 1];
		double[] liborArray = new double[_forwardTenorCount + 1];

		double[] multivariateRandomArray =
			lognormalLIBORVolatility.principalFactorSequenceGenerator().random();

		JulianDate targetPointDate = new JulianDate (viewDate);

		try {
			for (int forwardTenorIndex = 0; forwardTenorIndex <= _forwardTenorCount; ++forwardTenorIndex) {
				int targetPointDateInteger = targetPointDate.julian();

				double[] lognormalFactorPointVolatilityArray =
					lognormalLIBORVolatility.factorPointVolatility (evolutionDate, targetPointDateInteger);

				double[] continuousForwardVolatilityArray =
					lognormalLIBORVolatility.continuousForwardVolatility (
						targetPointDateInteger,
						forwardCurve
					);

				double libor = forwardCurve.forward (targetPointDateInteger);

				double liborDCF = forwardDCF * libor;
				double crossVolatilityDotProduct = 0.;
				double lognormalPointVolatilityModulus = 0.;
				double liborVolatilityMultiFactorRandom = 0.;
				tenorDateArray[forwardTenorIndex] = targetPointDateInteger;

				for (int lognormalFactorPointVolatilityIndex = 0;
					lognormalFactorPointVolatilityIndex < lognormalFactorPointVolatilityArray.length;
					++lognormalFactorPointVolatilityIndex)
				{
					lognormalPointVolatilityModulus +=
						lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
						lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex];
					crossVolatilityDotProduct +=
						lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
						continuousForwardVolatilityArray[lognormalFactorPointVolatilityIndex];
					liborVolatilityMultiFactorRandom +=
						lognormalFactorPointVolatilityArray[lognormalFactorPointVolatilityIndex] *
						multivariateRandomArray[lognormalFactorPointVolatilityIndex] *
						annualizedIncrementSQRT;
				}

				liborArray[forwardTenorIndex] = libor + annualizedIncrement * (
					forwardDerivative (forwardCurve, targetPointDateInteger) +
						libor * crossVolatilityDotProduct +
						(lognormalPointVolatilityModulus * libor * liborDCF / (1. + liborDCF))
					) + libor * liborVolatilityMultiFactorRandom;

				targetPointDate = targetPointDate.addTenor (forwardTenor);
			}

			return new PathwiseQMRealization (tenorDateArray, liborArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>LognormalLIBORCurveEvolver</i> Constructor
	 * 
	 * @param fundingLabel The Funding Latent State Label
	 * @param forwardLabel The Forward Latent State Label
	 * @param forwardTenorCount Number of Forward Tenors to Build the Span
	 * @param liborSegmentCustomBuilderControl LIBOR Span Segment Custom Builder Control Instance
	 * @param discountFactorSegmentCustomBuilderControl
	 * 	Discount Factor Span Segment Custom Builder Control Instance
	 * @param liborIncrementSegmentCustomBuilderControl
	 * 	LIBOR Increment Span Segment Custom Builder Control Instance
	 * @param discountFactorIncrementSegmentCustomBuilderControl
	 * 	Discount Factor Increment Span Segment Custom Builder Control Instance
	 * @param continuousForwardIncrementSegmentCustomBuilderControl
	 * 	Instantaneous Continuously Compounded Forward Rate Increment Span Segment Custom Builder Control
	 * 	 Instance
	 * @param spotRateIncrementSegmentCustomBuilderControl
	 * 	Spot Rate Increment Span Segment Custom Builder Control Instance
	 * @param instantaneousEffectiveForwardSegmentCustomBuilderControl
	 * 	Instantaneous Effective Annual Forward Rate Span Segment Custom Builder Control Instance
	 * @param instantaneousNominalForwardSegmentCustomBuilderControl
	 * 	Instantaneous Nominal Annual Forward Rate Span Segment Custom Builder Control Instance
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public LognormalLIBORCurveEvolver (
		final FundingLabel fundingLabel,
		final ForwardLabel forwardLabel,
		final int forwardTenorCount,
		final SegmentCustomBuilderControl liborSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl discountFactorSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl liborIncrementSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl discountFactorIncrementSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl continuousForwardIncrementSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl spotRateIncrementSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl instantaneousEffectiveForwardSegmentCustomBuilderControl,
		final SegmentCustomBuilderControl instantaneousNominalForwardSegmentCustomBuilderControl)
		throws Exception
	{
		if (null == (_fundingLabel = fundingLabel) ||
			null == (_forwardLabel = forwardLabel) ||
			1 >= (_forwardTenorCount = forwardTenorCount) ||
			null == liborSegmentCustomBuilderControl ||
			null == liborIncrementSegmentCustomBuilderControl ||
			null == discountFactorSegmentCustomBuilderControl ||
			null == discountFactorIncrementSegmentCustomBuilderControl ||
			null == continuousForwardIncrementSegmentCustomBuilderControl ||
			null == spotRateIncrementSegmentCustomBuilderControl ||
			null == instantaneousEffectiveForwardSegmentCustomBuilderControl)
		{
			throw new Exception ("LognormalLIBORCurveEvolver Constructor: Invalid Inputs");
		}

		_liborSegmentCustomBuilderControlArray = new SegmentCustomBuilderControl[_forwardTenorCount];
		_discountFactorSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_liborIncrementSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_discountFactorIncrementSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_continuousForwardIncrementSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_spotRateIncrementSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_instantaneousNominalForwardSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];
		_instantaneousEffectiveForwardSegmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[_forwardTenorCount];

		for (int forwardTenorIndex = 0; forwardTenorIndex < _forwardTenorCount; ++forwardTenorIndex) {
			_liborSegmentCustomBuilderControlArray[forwardTenorIndex] = liborSegmentCustomBuilderControl;
			_discountFactorSegmentCustomBuilderControlArray[forwardTenorIndex] =
				discountFactorSegmentCustomBuilderControl;
			_liborIncrementSegmentCustomBuilderControlArray[forwardTenorIndex] =
				liborIncrementSegmentCustomBuilderControl;
			_spotRateIncrementSegmentCustomBuilderControlArray[forwardTenorIndex] =
				spotRateIncrementSegmentCustomBuilderControl;
			_discountFactorIncrementSegmentCustomBuilderControlArray[forwardTenorIndex] =
				discountFactorIncrementSegmentCustomBuilderControl;
			_continuousForwardIncrementSegmentCustomBuilderControlArray[forwardTenorIndex] =
				continuousForwardIncrementSegmentCustomBuilderControl;
			_instantaneousNominalForwardSegmentCustomBuilderControlArray[forwardTenorIndex] =
				instantaneousNominalForwardSegmentCustomBuilderControl;
			_instantaneousEffectiveForwardSegmentCustomBuilderControlArray[forwardTenorIndex] =
				instantaneousEffectiveForwardSegmentCustomBuilderControl;
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
	 * Retrieve the Number of Forward Tenors comprising the Span Tenor
	 * 
	 * @return Number of Forward Tenors comprising the Span Tenor
	 */

	public int forwardTenorCount()
	{
		return _forwardTenorCount;
	}

	/**
	 * Retrieve the LIBOR Curve Segment Custom Builder Control Instance
	 * 
	 * @return The LIBOR Curve Segment Custom Builder Control Instance
	 */

	public SegmentCustomBuilderControl liborSegmentCustomBuilderControl()
	{
		return _liborSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Discount Factor Segment Custom Builder Control Instance
	 * 
	 * @return The Discount Factor Segment Custom Builder Control Instance
	 */

	public SegmentCustomBuilderControl discountFactorSegmentCustomBuilderControl()
	{
		return _discountFactorSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the LIBOR Increment Segment Custom Builder Control Instance
	 * 
	 * @return The LIBOR Increment Segment Custom Builder Control Instance
	 */

	public SegmentCustomBuilderControl liborIncrementSegmentCustomBuilderControl()
	{
		return _liborIncrementSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Discount Factor Increment Segment Custom Builder Control Instance
	 * 
	 * @return The Discount Factor Increment Segment Custom Builder Control Instance
	 */

	public SegmentCustomBuilderControl discountFactorIncrementSegmentCustomBuilderControl()
	{
		return _discountFactorIncrementSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Instantaneous Continuously Compounded Forward Rate Increment Segment Custom Builder
	 *  Control Instance
	 * 
	 * @return The Instantaneous Continuously Compounded Forward Rate Increment Segment Custom Builder
	 *  Control Instance
	 */

	public SegmentCustomBuilderControl continuousForwardIncrementSegmentCustomBuilderControl()
	{
		return _continuousForwardIncrementSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Spot Rate Increment Segment Custom Builder Control Instance
	 * 
	 * @return The Spot Rate Increment Segment Custom Builder Control Instance
	 */

	public SegmentCustomBuilderControl spotRateIncrementSegmentCustomBuilderControl()
	{
		return _spotRateIncrementSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Instantaneous Effective Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 * 
	 * @return The Instantaneous Effective Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 */

	public SegmentCustomBuilderControl instantaneousEffectiveForwardSegmentCustomBuilderControl()
	{
		return _instantaneousEffectiveForwardSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Retrieve the Instantaneous Nominal Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 * 
	 * @return The Instantaneous Nominal Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 */

	public SegmentCustomBuilderControl instantaneousNominalForwardSegmentCustomBuilderControl()
	{
		return _instantaneousNominalForwardSegmentCustomBuilderControlArray[0];
	}

	/**
	 * Evolve the Latent State and return the LSQM Curve Update
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param spotTimeIncrement The Spot Evolution Increment
	 * @param previousLSQMCurveUpdate The Previous LSQM Curve Update
	 * 
	 * @return The LSQM Curve Update
	 */

	@Override public BGMCurveUpdate evolve (
		final int spotDate,
		final int viewDate,
		final int spotTimeIncrement,
		final LSQMCurveUpdate previousLSQMCurveUpdate)
	{
		if (spotDate > viewDate ||
			null == previousLSQMCurveUpdate || !(previousLSQMCurveUpdate instanceof BGMCurveUpdate))
		{
			return null;
		}

		BGMForwardTenorSnap[] bgmForwardTenorSnapArray = new BGMForwardTenorSnap[_forwardTenorCount + 1];
		BGMCurveUpdate previousBGMCurveUpdate = (BGMCurveUpdate) previousLSQMCurveUpdate;

		double annualizedIncrementSQRT = Math.sqrt (1. * spotTimeIncrement / 365.25);

		ForwardCurve forwardCurve = previousBGMCurveUpdate.forwardCurve();

		String forwardTenor = _forwardLabel.tenor();

		JulianDate targetPointDate = new JulianDate (viewDate);

		MergedDiscountForwardCurve discountCurve = previousBGMCurveUpdate.discountCurve();

		LognormalLIBORVolatility lognormalLIBORVolatility =
			previousBGMCurveUpdate.lognormalLIBORVolatility();

		try {
			for (int forwardTenorIndex = 0; forwardTenorIndex <= _forwardTenorCount; ++forwardTenorIndex) {
				if (null == (
					bgmForwardTenorSnapArray[forwardTenorIndex] = timeSnap (
						spotDate,
						targetPointDate.julian(),
						spotTimeIncrement,
						annualizedIncrementSQRT,
						forwardTenor,
						forwardCurve,
						discountCurve,
						lognormalLIBORVolatility
					)
				) || null == (targetPointDate = targetPointDate.addTenor (forwardTenor)))
				{
					return null;
				}
			}

			BGMTenorNodeSequence bgmTenorNodeSequence = new BGMTenorNodeSequence (bgmForwardTenorSnapArray);

			BoundarySettings boundarySettings = BoundarySettings.NaturalStandard();

			String forwardLabelName = _forwardLabel.fullyQualifiedName();

			String fundingLabelName = _fundingLabel.fullyQualifiedName();

			int[] tenorDateArray = bgmTenorNodeSequence.dateArray();

			return BGMCurveUpdate.Create (
				_fundingLabel,
				_forwardLabel,
				spotDate,
				spotDate + spotTimeIncrement,
				new BasisSplineForwardRate (
					_forwardLabel,
					new OverlappingStretchSpan (
						MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
							forwardLabelName + "_QM_LIBOR",
							tenorDateArray,
							bgmTenorNodeSequence.liborArray(),
							_liborSegmentCustomBuilderControlArray,
							null,
							boundarySettings,
							MultiSegmentSequence.CALIBRATE
						)
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						forwardLabelName + "_INCREMENT",
						tenorDateArray,
						bgmTenorNodeSequence.liborIncrementArray(),
						_liborIncrementSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				new DiscountFactorDiscountCurve (
					_forwardLabel.currency(),
					new OverlappingStretchSpan (
						MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
							fundingLabelName + "_QM_DISCOUNTFACTOR",
							tenorDateArray,
							bgmTenorNodeSequence.discountFactorArray(),
							_discountFactorSegmentCustomBuilderControlArray,
							null,
							boundarySettings,
							MultiSegmentSequence.CALIBRATE
						)
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						fundingLabelName + "_INCREMENT",
						tenorDateArray,
						bgmTenorNodeSequence.discountFactorIncrementArray(),
						_discountFactorIncrementSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						forwardLabelName + "_CONT_FWD_INCREMENT",
						tenorDateArray,
						bgmTenorNodeSequence.continuousForwardRateIncrementArray(),
						_continuousForwardIncrementSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						forwardLabelName + "_SPOT_RATE_INCREMENT",
						tenorDateArray,
						bgmTenorNodeSequence.spotRateIncrementArray(),
						_spotRateIncrementSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						forwardLabelName + "_EFFECTIVE_ANNUAL_FORWARD",
						tenorDateArray,
						bgmTenorNodeSequence.instantaneousEffectiveForwardRateArray(),
						_instantaneousEffectiveForwardSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				new OverlappingStretchSpan (
					MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
						forwardLabelName + "_NOMINAL_ANNUAL_FORWARD",
						tenorDateArray,
						bgmTenorNodeSequence.instantaneousNominalForwardRateArray(),
						_instantaneousNominalForwardSegmentCustomBuilderControlArray,
						null,
						boundarySettings,
						MultiSegmentSequence.CALIBRATE
					)
				),
				previousBGMCurveUpdate.lognormalLIBORVolatility()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Simulate the Principal Metric from the Start to the End Date
	 * 
	 * @param evolutionStartDate The Evolution Start Date
	 * @param evolutionFinishDate The Evolution Finish Date
	 * @param evolutionIncrement The Evolution Increment
	 * @param viewDate The View Date
	 * @param startingLSQMCurveUpdate The Starting State Metrics
	 * @param simulationCount Number of Simulations
	 * 
	 * @return The Array of the Evolved Tenor LIBOR's
	 */

	@Override public double[][] simulatePrincipalMetric (
		final int evolutionStartDate,
		final int evolutionFinishDate,
		final int evolutionIncrement,
		final int viewDate,
		final LSQMCurveUpdate startingLSQMCurveUpdate,
		final int simulationCount)
	{
		if (evolutionStartDate > viewDate ||
			evolutionFinishDate <= evolutionStartDate ||
			evolutionFinishDate > viewDate ||
			evolutionIncrement <= 0. ||
			null == startingLSQMCurveUpdate || !(startingLSQMCurveUpdate instanceof BGMCurveUpdate) ||
			1 >= simulationCount)
		{
			return null;
		}

		BGMCurveUpdate bgmCurveUpdate = (BGMCurveUpdate) startingLSQMCurveUpdate;

		LognormalLIBORVolatility lognormalLIBORVolatility = bgmCurveUpdate.lognormalLIBORVolatility();

		String forwardLabel = _forwardLabel.fullyQualifiedName() + "_QM_LIBOR";

		ForwardCurve forwardCurve = bgmCurveUpdate.forwardCurve();

		String forwardTenor = _forwardLabel.tenor();

		int timeStepCount = ((evolutionFinishDate - evolutionStartDate) / evolutionIncrement) + 1;
		double[][] tenorLIBORGrid = new double[timeStepCount][_forwardTenorCount + 1];
		double annualizedIncrement = 1. * evolutionIncrement / 365.25;
		double forwardDCF = Double.NaN;

		double annualizedIncrementSQRT = Math.sqrt (annualizedIncrement);

		BoundarySettings boundarySettings = BoundarySettings.NaturalStandard();

		try {
			forwardDCF = Helper.TenorToYearFraction (forwardTenor);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int timeStepIndex = 0; timeStepIndex < timeStepCount; ++timeStepIndex) {
			for (int forwardTenorIndex = 0 ; forwardTenorIndex <= _forwardTenorCount; ++forwardTenorIndex) {
				tenorLIBORGrid[timeStepIndex][forwardTenorIndex] = 0.;
			}
		}

		for (int simulationIndex = 0; simulationIndex < simulationCount; ++simulationIndex) {
			int evolutionTimeIndex = 0;
			ForwardCurve liborForwardCurve = forwardCurve;

			for (int evolutionDate = evolutionStartDate;
				evolutionDate <= evolutionFinishDate;
				evolutionDate += evolutionIncrement)
			{
				PathwiseQMRealization pathwiseQMRealization = simulateLIBOR (
					evolutionDate,
					viewDate,
					annualizedIncrement,
					annualizedIncrementSQRT,
					liborForwardCurve,
					forwardTenor,
					forwardDCF,
					lognormalLIBORVolatility
				);

				if (null == pathwiseQMRealization) {
					return null;
				}

				double[] simulatedLIBORArray = pathwiseQMRealization.pointStateQMRealizationArray();

				try {
					liborForwardCurve = new BasisSplineForwardRate (
						_forwardLabel,
						new OverlappingStretchSpan (
							MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
								forwardLabel + "_QM_LIBOR",
								pathwiseQMRealization.targetDateNodeArray(),
								simulatedLIBORArray,
								_liborSegmentCustomBuilderControlArray,
								null,
								boundarySettings,
								MultiSegmentSequence.CALIBRATE
							)
						)
					);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}

				for (int forwardTenorIndex = 0;
					forwardTenorIndex <= _forwardTenorCount;
					++forwardTenorIndex)
				{
					tenorLIBORGrid[evolutionTimeIndex][forwardTenorIndex] +=
						simulatedLIBORArray[forwardTenorIndex];
				}

				++evolutionTimeIndex;
			}
		}

		for (int timeStepIndex = 0; timeStepIndex < timeStepCount; ++timeStepIndex) {
			for (int forwardTenorIndex = 0; forwardTenorIndex <= _forwardTenorCount; ++forwardTenorIndex) {
				tenorLIBORGrid[timeStepIndex][forwardTenorIndex] /= simulationCount;
			}
		}

		return tenorLIBORGrid;
	}

	/**
	 * Construct an Array of Forward Curves that Result from the Simulation
	 * 
	 * @param evolutionStartDate The Start Date of the Simulation
	 * @param evolutionFinishDate The Finish Date of the Simulation
	 * @param evolutionIncrement The Simulation Evolution Increment
	 * @param viewDate The Forward View Date
	 * @param startingLSQMCurveUpdate The Initial/Starting LSQM State
	 * @param simulationCount Number of Simulations
	 * 
	 * @return The Array of Forward Curves that Result from the Simulation
	 */

	public ForwardCurve[] simulateTerminalLatentState (
		final int evolutionStartDate,
		final int evolutionFinishDate,
		final int evolutionIncrement,
		final int viewDate,
		final LSQMCurveUpdate startingLSQMCurveUpdate,
		final int simulationCount)
	{
		if (evolutionStartDate > viewDate ||
			evolutionFinishDate <= evolutionStartDate ||
			evolutionFinishDate > viewDate ||
			evolutionIncrement <= 0. ||
			null == startingLSQMCurveUpdate || !(startingLSQMCurveUpdate instanceof BGMCurveUpdate) ||
			1 >= simulationCount)
		{
			return null;
		}

		BGMCurveUpdate bgmCurveUpdate = (BGMCurveUpdate) startingLSQMCurveUpdate;

		LognormalLIBORVolatility lognormalLIBORVolatility = bgmCurveUpdate.lognormalLIBORVolatility();

		String forwardLabel = _forwardLabel.fullyQualifiedName() + "_QM_LIBOR";

		ForwardCurve forwardCurve = bgmCurveUpdate.forwardCurve();

		String forwardTenor = _forwardLabel.tenor();

		ForwardCurve[] liborForwardCurveArray = new ForwardCurve[simulationCount];
		double annualizedIncrement = 1. * evolutionIncrement / 365.25;
		double forwardDCF = Double.NaN;

		double annualizedIncrementSQRT = Math.sqrt (annualizedIncrement);

		BoundarySettings boundarySettings = BoundarySettings.NaturalStandard();

		try {
			forwardDCF = Helper.TenorToYearFraction (forwardTenor);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int simulationIndex = 0; simulationIndex < simulationCount; ++simulationIndex) {
			System.out.println ("\t\tSimulation #" + (simulationIndex + 1));

			ForwardCurve liborForwardCurve = forwardCurve;

			for (int evolutionDate = evolutionStartDate;
				evolutionDate <= evolutionFinishDate;
				evolutionDate += evolutionIncrement)
			{
				PathwiseQMRealization pathwiseQMRealization = simulateLIBOR (
					evolutionDate,
					viewDate,
					annualizedIncrement,
					annualizedIncrementSQRT,
					liborForwardCurve,
					forwardTenor,
					forwardDCF,
					lognormalLIBORVolatility
				);

				if (null == pathwiseQMRealization) {
					return null;
				}

				try {
					liborForwardCurve = new BasisSplineForwardRate (
						_forwardLabel,
						new OverlappingStretchSpan (
							MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
								forwardLabel + "_QM_LIBOR",
								pathwiseQMRealization.targetDateNodeArray(),
								pathwiseQMRealization.pointStateQMRealizationArray(),
								_liborSegmentCustomBuilderControlArray,
								null,
								boundarySettings,
								MultiSegmentSequence.CALIBRATE
							)
						)
					);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}
			}

			liborForwardCurveArray[simulationIndex] = liborForwardCurve;
		}

		return liborForwardCurveArray;
	}
}
