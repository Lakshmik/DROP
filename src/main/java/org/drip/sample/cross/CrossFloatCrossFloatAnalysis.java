
package org.drip.sample.cross;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.params.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.identifier.*;

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
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * <i>CrossFloatCrossFloatAnalysis</i> demonstrates the impact of Funding Volatility, Forward Volatility, and
 * 	Funding/Forward, Funding/FX, and Forward/FX Correlation for each of the FRI's on the Valuation of a
 * 	float-float swap with a 3M EUR Floater leg that pays in USD, and a 6M EUR Floater leg that pays in USD.
 *	Comparison is done across MTM and non-MTM fixed Leg Counterparts.
 *  
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/cross/README.md">Single/Dual Stream XCCY Component</a></li>
 *  </ul>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CrossFloatCrossFloatAnalysis
{

	private static final FloatFloatComponent MakeFloatFloatSwap (
		final JulianDate effectiveDate,
		final boolean fxMTM,
		final String payCurrency,
		final String couponCurrency,
		final String maturityTenor,
		final int tenorInMonthsReference,
		final int tenorInMonthsDerived)
		throws Exception
	{
		String derivedTenor = tenorInMonthsDerived + "M";
		String referenceTenor = tenorInMonthsReference + "M";

		FixingSetting fixingSetting = fxMTM ? null : new FixingSetting (
			FixingSetting.FIXING_PRESET_STATIC,
			null,
			effectiveDate.julian()
		);

		return new FloatFloatComponent (
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						referenceTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonthsReference,
						referenceTenor,
						payCurrency,
						null,
						-1.,
						null,
						null,
						fixingSetting,
						null
					),
					new ComposableFloatingUnitSetting (
						referenceTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (couponCurrency, tenorInMonthsReference + "M"),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						derivedTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonthsDerived,
						derivedTenor,
						payCurrency,
						null,
						1.,
						null,
						null,
						fixingSetting,
						null
					),
					new ComposableFloatingUnitSetting (
						derivedTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (couponCurrency, derivedTenor),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new CashSettleParams (0, payCurrency, 0)
		);
	}

	private static final void SetMarketParams (
		final int valuationDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel1,
		final ForwardLabel forwardLabel2,
		final FundingLabel fundingLabel,
		final FXLabel fxLabel,
		final double forward1Volatility,
		final double forward2Volatility,
		final double fundingVolatility,
		final double fxVolatility,
		final double forward1FundingCorrelation,
		final double forward2FundingCorrelation,
		final double forward1FXCorrelation,
		final double forward2FXCorrelation,
		final double fundingFXCorrelation)
		throws Exception
	{
		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (forwardLabel1),
				forwardLabel1.currency(),
				forward1Volatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (forwardLabel2),
				forwardLabel2.currency(),
				forward2Volatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (fundingLabel),
				forwardLabel1.currency(),
				fundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setFXVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (fxLabel),
				forwardLabel1.currency(),
				fxVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel1,
			fundingLabel,
			new Flat (forward1FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel2,
			fundingLabel,
			new Flat (forward2FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardLabel1,
			fxLabel,
			new Flat (forward1FXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardLabel2,
			fxLabel,
			new Flat (forward2FXCorrelation)
		);

		curveSurfaceQuoteContainer.setFundingFXCorrelation (
			fundingLabel,
			fxLabel,
			new Flat (fundingFXCorrelation)
		);
	}

	private static final void VolatilityCorrelationScenario (
		final FloatFloatComponent[] floatFloatComponentArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel1,
		final ForwardLabel forwardLabel2,
		final FundingLabel fundingLabel,
		final FXLabel fxLabel,
		final double forward1Volatility,
		final double forward2Volatility,
		final double fundingVolatility,
		final double fxVolatility,
		final double forward1FundingCorrelation,
		final double forward2FundingCorrelation,
		final double forward1FXCorrelation,
		final double forward2FXCorrelation,
		final double fundingFXCorrelation)
		throws Exception
	{
		SetMarketParams (
			valuationParams.valueDate(),
			curveSurfaceQuoteContainer,
			forwardLabel1,
			forwardLabel2,
			fundingLabel,
			fxLabel,
			forward1Volatility,
			forward2Volatility,
			fundingVolatility,
			fxVolatility,
			forward1FundingCorrelation,
			forward2FundingCorrelation,
			forward1FXCorrelation,
			forward2FXCorrelation,
			fundingFXCorrelation
		);

		String dump = "\t|| [" + FormatUtil.FormatDouble (forward1Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fxVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward1FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward1FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingFXCorrelation, 2, 0, 100.) + "%] = ";

		for (int floatFloatIndex = 0; floatFloatIndex < floatFloatComponentArray.length; ++floatFloatIndex) {
			CaseInsensitiveTreeMap<Double> measureMap = floatFloatComponentArray[floatFloatIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			dump += (
				0 != floatFloatIndex ? " || " : ""
			) + FormatUtil.FormatDouble (
				measureMap.get ("ReferenceCumulativeConvexityAdjustmentPremium"),
				2,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				measureMap.get ("DerivedCumulativeConvexityAdjustmentPremium"),
				2,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				measureMap.get ("CumulativeConvexityAdjustmentPremium"),
				2,
				0,
				10000.
			) + " ||";
		}

		System.out.println (dump);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double usdFundingRate = 0.02;
		double eur3MForwardRate = 0.02;
		double eur6MForwardRate = 0.025;
		double usdEURFXRate = 1. / 1.35;
		double[] eurForward3MVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] eurForward6MVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] usdFundingVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] usdEURFXVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] eur3MUSDFundingCorrelationArray =
		{
			-0.20,
			 0.25
		};
		double[] eur6MUSDFundingCorrelationArray =
		{
			-0.20,
			 0.25
		};
		double[] eur3MUSDEURFXCorrelationArray =
		{
			-0.20,
			 0.25
		};
		double[] eur6MUSDEURFXCorrelationArray =
		{
			-0.20,
			 0.25
		};
		double[] usdFundingUSDEURFXCorrelationArray =
		{
			-0.20,
			 0.25
		};

		JulianDate today = DateUtil.Today();

		CurrencyPair currencyPair = CurrencyPair.FromCode ("USD/EUR");

		ForwardLabel eur3MForwardLabel = ForwardLabel.Create ("EUR", "3M");

		ForwardLabel eur6MForwardLabel = ForwardLabel.Create ("EUR", "6M");

		ValuationParams valuationParams = new ValuationParams (today, today, "EUR");

		FloatFloatComponent mtmFloatFloat = MakeFloatFloatSwap (today, true, "USD", "EUR", "2Y", 6, 3);

		FloatFloatComponent nonMTMFloatFloat = MakeFloatFloatSwap (today, false, "USD", "EUR", "2Y", 6, 3);

		nonMTMFloatFloat.setPrimaryCode ("EUR__USD__NONMTM::FLOAT::3M::6M::2Y");

		mtmFloatFloat.setPrimaryCode ("EUR__USD__MTM::FLOAT::3M::6M::2Y");

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (today, eur3MForwardLabel, eur3MForwardRate)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (today, eur6MForwardLabel, eur6MForwardRate)
		);

		curveSurfaceQuoteContainer.setFundingState (
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (today, "USD", usdFundingRate)
		);

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				fxLabel.fullyQualifiedName(),
				today,
				currencyPair,
				new String[]
				{
					"10Y"
				},
				new double[]
				{
					usdEURFXRate
				},
				usdEURFXRate
			)
		);

		curveSurfaceQuoteContainer.setFixing (today, fxLabel, usdEURFXRate);

		for (double eurForward3MVolatility : eurForward3MVolatilityArray) {
			for (double eurForward6MVolatility : eurForward6MVolatilityArray) {
				for (double usdFundingVolatility : usdFundingVolatilityArray) {
					for (double usdEURFXVolatility : usdEURFXVolatilityArray) {
						for (double eur3MUSDFundingCorrelation : eur3MUSDFundingCorrelationArray) {
							for (double eur6MUSDFundingCorrelation : eur6MUSDFundingCorrelationArray) {
								for (double eur3MUSDEURFXCorrelation : eur3MUSDEURFXCorrelationArray) {
									for (double eur6MUSDEURFXCorrelation : eur6MUSDEURFXCorrelationArray) {
										for (double usdFundingUSDEURFXCorrelation :
											usdFundingUSDEURFXCorrelationArray)
										{
											VolatilityCorrelationScenario (
												new FloatFloatComponent[]
												{
													mtmFloatFloat,
													nonMTMFloatFloat
												},
												valuationParams,
												curveSurfaceQuoteContainer,
												eur3MForwardLabel,
												eur6MForwardLabel,
												FundingLabel.Standard ("USD"),
												fxLabel,
												eurForward3MVolatility,
												eurForward6MVolatility,
												usdFundingVolatility,
												usdEURFXVolatility,
												eur3MUSDFundingCorrelation,
												eur6MUSDFundingCorrelation,
												eur3MUSDEURFXCorrelation,
												eur6MUSDEURFXCorrelation,
												usdFundingUSDEURFXCorrelation
											);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		EnvManager.TerminateEnv();
	}
}
