
package org.drip.sample.cross;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.fx.ComponentPair;
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
 * <i>FixFloatFixFloatAnalysis</i> demonstrates the Funding Volatility, Forward Volatility, FX Volatility,
 * 	Funding/Forward Correlation, Funding/FX Correlation, and Forward/FX Correlation across the 2 currencies
 * 	(USD and EUR) on the Valuation of the Cross Currency Basis Swap built out of a pair of fix-float swaps.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/cross/README.md">Single/Dual Stream XCCY Component</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixFloatFixFloatAnalysis
{

	private static final FixFloatComponent MakeFixFloatSwap (
		final JulianDate effectiveDate,
		final boolean fxMTM,
		final String payCurrency,
		final String fixedCouponCurrency,
		final String maturityTenor,
		final int tenorInMonths)
		throws Exception
	{
		String tenor = tenorInMonths + "M";

		return new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"6M",
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						2,
						"6M",
						payCurrency,
						null,
						1.,
						null,
						null,
						fxMTM ? null : new FixingSetting (
							FixingSetting.FIXING_PRESET_STATIC,
							null,
							effectiveDate.julian()
						),
						null
					),
					new UnitCouponAccrualSetting (
						2,
						"Act/360",
						false,
						"Act/360",
						false,
						fixedCouponCurrency,
						false,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						"6M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						0.02,
						0.,
						fixedCouponCurrency
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						tenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonths,
						tenor,
						payCurrency,
						null,
						-1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						tenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (payCurrency, tenor),
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

	private static final void VolCorrScenario (
		final ComponentPair[] componentPairArray,
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
			forward2FXCorrelation,
			fundingFXCorrelation
		);

		String dump = "\t|| [" + FormatUtil.FormatDouble (forward1Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fxVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward1FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forward2FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingFXCorrelation, 2, 0, 100.) + "%] = ";

		for (int componentPairIndex = 0;
			componentPairIndex < componentPairArray.length;
			++componentPairIndex)
		{
			CaseInsensitiveTreeMap<Double> measureMap = componentPairArray[componentPairIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			dump += (
				0 == componentPairIndex ? "" : " || "
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
			);
		}

		System.out.println (dump + " ||");
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

		double usdFundingRate = 0.03;
		double usdEURFXRate = 1. / 1.34;
		double eur3MForwardRate = 0.0175;
		double usd3MForwardRate = 0.0275;
		double[] usdEURFXVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] usdFundingVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] eur3MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] usd3MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] eur3MForwardUSDEURFXCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] usdFundingUSDEURFXCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] eur3MForwardUSDFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] usd3MForwardUSDFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};

		JulianDate today = DateUtil.Today();

		FundingLabel fundingLabel = FundingLabel.Standard ("USD");

		CurrencyPair currencyPair = CurrencyPair.FromCode ("USD/EUR");

		ForwardLabel eur3MForwardLabel = ForwardLabel.Create ("EUR", "3M");

		ForwardLabel usd3MForwardLabel = ForwardLabel.Create ("USD", "3M");

		ValuationParams valuationParams = new ValuationParams (today, today, "USD");

		FixFloatComponent nonMTMUSDFixFloat = MakeFixFloatSwap (today, false, "USD", "USD", "2Y", 3);

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFixing (today, fxLabel, usdEURFXRate);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (today, usd3MForwardLabel, usd3MForwardRate)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (today, eur3MForwardLabel, eur3MForwardRate)
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

		for (double usd3MForwardVolatility : usd3MForwardVolatilityArray) {
			for (double eur3MForwardVolatility : eur3MForwardVolatilityArray) {
				for (double usdFundingVolatility : usdFundingVolatilityArray) {
					for (double usdEURFXVolatility : usdEURFXVolatilityArray) {
						for (double usd3MForwardUSDFundingCorrelation :
							usd3MForwardUSDFundingCorrelationArray)
						{
							for (double eur3MForwardUSDFundingCorrelation :
								eur3MForwardUSDFundingCorrelationArray)
							{
								for (double eur3MForwardUSDEURFXCorrelation :
									eur3MForwardUSDEURFXCorrelationArray)
								{
									for (double usdFundingUSDEURFXCorrelation :
										usdFundingUSDEURFXCorrelationArray)
									{
										VolCorrScenario (
											new ComponentPair[]
												{
												new ComponentPair (
													"FFFF_MTM",
													nonMTMUSDFixFloat,
													MakeFixFloatSwap (today, true, "USD", "EUR", "2Y", 3),
													null
												),
												new ComponentPair (
													"FFFF_Non_MTM",
													nonMTMUSDFixFloat,
													MakeFixFloatSwap (today, false, "USD", "EUR", "2Y", 3),
													null
												)
											},
											valuationParams,
											curveSurfaceQuoteContainer,
											usd3MForwardLabel,
											eur3MForwardLabel,
											fundingLabel,
											fxLabel,
											usd3MForwardVolatility,
											eur3MForwardVolatility,
											usdFundingVolatility,
											usdEURFXVolatility,
											usd3MForwardUSDFundingCorrelation,
											eur3MForwardUSDFundingCorrelation,
											eur3MForwardUSDEURFXCorrelation,
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

		EnvManager.TerminateEnv();
	}
}
