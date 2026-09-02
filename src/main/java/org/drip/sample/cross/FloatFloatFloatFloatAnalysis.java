
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
 * <i>FloatFloatFloatFloatAnalysis</i> demonstrates the Funding Volatility, Forward Volatility, FX
 * 	Volatility, Funding/Forward Correlation, Funding/FX Correlation, and Forward/FX Correlation of the Cross
 * 	Currency Basis Swap built out of a pair of float-float swaps.
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

public class FloatFloatFloatFloatAnalysis
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
		final int valueDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardReferenceLabel1,
		final ForwardLabel forwardReferenceLabel2,
		final ForwardLabel forwardDerivedLabel1,
		final ForwardLabel forwardDerivedLabel2,
		final FundingLabel fundingLabel,
		final FXLabel fxLabel,
		final double forwardReference1Volatility,
		final double forwardReference2Volatility,
		final double forwardDerived1Volatility,
		final double forwardDerived2Volatility,
		final double fundingVolatility,
		final double fxVolatility,
		final double forwardReference1FundingCorrelation,
		final double forwardReference2FundingCorrelation,
		final double forwardDerived1FundingCorrelation,
		final double forwardDerived2FundingCorrelation,
		final double forwardReference1FXCorrelation,
		final double forwardReference2FXCorrelation,
		final double forwardDerived1FXCorrelation,
		final double forwardDerived2FXCorrelation,
		final double fundingFXCorrelation)
		throws Exception
	{
		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardReferenceLabel1),
				forwardReferenceLabel1.currency(),
				forwardReference1Volatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardReferenceLabel2),
				forwardReferenceLabel2.currency(),
				forwardReference2Volatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardDerivedLabel1),
				forwardDerivedLabel1.currency(),
				forwardDerived1Volatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardDerivedLabel2),
				forwardDerivedLabel2.currency(),
				forwardDerived2Volatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (fundingLabel),
				forwardDerivedLabel1.currency(),
				fundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setFXVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (fxLabel),
				forwardDerivedLabel1.currency(),
				fxVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardReferenceLabel1,
			fundingLabel,
			new Flat (forwardReference1FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardReferenceLabel2,
			fundingLabel,
			new Flat (forwardReference2FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardDerivedLabel1,
			fundingLabel,
			new Flat (forwardDerived1FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardDerivedLabel2,
			fundingLabel,
			new Flat (forwardDerived2FundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardReferenceLabel1,
			fxLabel,
			new Flat (forwardReference1FXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardReferenceLabel2,
			fxLabel,
			new Flat (forwardReference2FXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardDerivedLabel1,
			fxLabel,
			new Flat (forwardDerived1FXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			forwardDerivedLabel2,
			fxLabel,
			new Flat (forwardDerived2FXCorrelation)
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
		final ForwardLabel forwardReferenceLabel1,
		final ForwardLabel forwardReferenceLabel2,
		final ForwardLabel forwardDerivedLabel1,
		final ForwardLabel forwardDerivedLabel2,
		final FundingLabel fundingLabel,
		final FXLabel fxLabel,
		final double forwardReference1Volatility,
		final double forwardReference2Volatility,
		final double forwardDerived1Volatility,
		final double forwardDerived2Volatility,
		final double fundingVolatility,
		final double fxVolatility,
		final double forwardReference1FundingCorrelation,
		final double forwardReference2FundingCorrelation,
		final double forwardDerived1FundingCorrelation,
		final double forwardDerived2FundingCorrelation,
		final double forwardReference1FXCorrelation,
		final double forwardReference2FXCorrelation,
		final double forwardDerived1FXCorrelation,
		final double forwardDerived2FXCorrelation,
		final double fundingFXCorrelation)
		throws Exception
	{
		SetMarketParams (
			valuationParams.valueDate(),
			curveSurfaceQuoteContainer,
			forwardReferenceLabel1,
			forwardReferenceLabel2,
			forwardDerivedLabel1,
			forwardDerivedLabel2,
			fundingLabel,
			fxLabel,
			forwardReference1Volatility,
			forwardReference2Volatility,
			forwardDerived1Volatility,
			forwardDerived2Volatility,
			fundingVolatility,
			fxVolatility,
			forwardReference1FundingCorrelation,
			forwardReference2FundingCorrelation,
			forwardDerived1FundingCorrelation,
			forwardDerived2FundingCorrelation,
			forwardReference1FXCorrelation,
			forwardReference2FXCorrelation,
			forwardDerived1FXCorrelation,
			forwardDerived2FXCorrelation,
			fundingFXCorrelation
		);

		String dump = "\t|| [" + FormatUtil.FormatDouble (forwardReference1Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardReference2Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived1Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived2Volatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fxVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardReference1FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardReference2FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived1FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived2FundingCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardReference1FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardReference2FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived1FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardDerived2FXCorrelation, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingFXCorrelation, 2, 0, 100.) + "%] =>";

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

			dump += (0 == componentPairIndex ? "" : " || ") + FormatUtil.FormatDouble (
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

		String derivedCurrency = "EUR";
		String referenceCurrency = "USD";

		double referenceFundingRate = 0.02;
		double derived3MForwardRate = 0.00375;
		double derived6MForwardRate = 0.00625;
		double reference3MForwardRate = 0.00750;
		double reference6MForwardRate = 0.01000;
		double referenceDerivedFXRate = 1. / 1.28;

		double[] derived3MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] derived6MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] referenceFundingVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] reference3MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] reference6MForwardVolatilityArray =
		{
			0.1,
			0.4
		};
		double[] referenceDerivedFXVolatilityArray =
		{
			0.1,
			0.4
		};

		double[] derived3MForwardFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] derived6MForwardFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] reference3MForwardFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] reference6MForwardFundingCorrelationArray =
		{
			-0.1,
			 0.2
		};

		double[] derived3MForwardFXCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] derived6MForwardFXCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] reference3MForwardFXCorrelationArray =
		{
			-0.1,
			 0.2
		};
		double[] reference6MForwardFXCorrelationArray =
		{
			-0.1,
			 0.2
		};

		double[] fundingFXCorrelationArray =
		{
			-0.1,
			 0.2
		};

		JulianDate today = DateUtil.Today();

		ValuationParams valuationParams = new ValuationParams (today, today, "USD");

		FundingLabel fundingLabelReference = FundingLabel.Standard (referenceCurrency);

		ForwardLabel derived3MForwardLabel = ForwardLabel.Create (derivedCurrency, "3M");

		ForwardLabel derived6MForwardLabel = ForwardLabel.Create (derivedCurrency, "6M");

		ForwardLabel reference3MForwardLabel = ForwardLabel.Create (referenceCurrency, "3M");

		ForwardLabel reference6MForwardLabel = ForwardLabel.Create (referenceCurrency, "6M");

		CurrencyPair currencyPair = CurrencyPair.FromCode (referenceCurrency + "/" + derivedCurrency);

		FloatFloatComponent nonMTMReferenceFloatFloat = MakeFloatFloatSwap (
			today,
			false,
			referenceCurrency,
			referenceCurrency,
			"2Y",
			6,
			3
		);

		nonMTMReferenceFloatFloat.setPrimaryCode (
			"FLOAT::FLOAT::" + referenceCurrency + "::" + referenceCurrency + "_3M::" + referenceCurrency +
				"_6M::2Y"
		);

		FloatFloatComponent mtmDerivedFloatFloat = MakeFloatFloatSwap (
			today,
			true,
			referenceCurrency,
			derivedCurrency,
			"2Y",
			6,
			3
		);

		mtmDerivedFloatFloat.setPrimaryCode (
			"FLOAT::FLOAT::MTM::" + referenceCurrency + "::" + derivedCurrency + "_3M::" + derivedCurrency +
				"_6M::2Y"
		);

		ComponentPair mtmComponentPair = new ComponentPair (
			"FFFF_MTM",
			nonMTMReferenceFloatFloat,
			mtmDerivedFloatFloat,
			null
		);

		FloatFloatComponent nonMTMDerivedFloatFloat = MakeFloatFloatSwap (
			today,
			false,
			referenceCurrency,
			derivedCurrency,
			"2Y",
			6,
			3
		);

		nonMTMDerivedFloatFloat.setPrimaryCode (
			"FLOAT::FLOAT::NONMTM::" + referenceCurrency + "::" + derivedCurrency + "_3M::" + derivedCurrency
				+ "_6M::2Y"
		);

		ComponentPair nonMTMComponentPair = new ComponentPair (
			"FFFF_NonMTM",
			nonMTMReferenceFloatFloat,
			nonMTMDerivedFloatFloat,
			null
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		curveSurfaceQuoteContainer.setFixing (today, fxLabel, referenceDerivedFXRate);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				reference3MForwardLabel,
				reference3MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				reference6MForwardLabel,
				reference6MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				derived3MForwardLabel,
				derived3MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				derived6MForwardLabel,
				derived6MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setFundingState (
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (
				today,
				referenceCurrency,
				referenceFundingRate
			)
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
					referenceDerivedFXRate
				},
				referenceDerivedFXRate
			)
		);

		for (double reference3MForwardVolatility : reference3MForwardVolatilityArray) {
			for (double reference6MForwardVolatility : reference6MForwardVolatilityArray) {
				for (double derived3MForwardVolatility : derived3MForwardVolatilityArray) {
					for (double derived6MForwardVolatility : derived6MForwardVolatilityArray) {
						for (double referenceFundingVolatility : referenceFundingVolatilityArray) {
							for (double referenceDerivedFXVolatility : referenceDerivedFXVolatilityArray) {
								for (double reference3MForwardFundingCorrelation :
										reference3MForwardFundingCorrelationArray)
								{
									for (double reference6MForwardFundingCorrelation :
										reference6MForwardFundingCorrelationArray)
									{
										for (double derived3MForwardFundingCorrelation :
											derived3MForwardFundingCorrelationArray)
										{
											for (double derived6MForwardFundingCorrelation :
												derived6MForwardFundingCorrelationArray)
											{
												for (double reference3MForwardFXCorrelation :
													reference3MForwardFXCorrelationArray)
												{
													for (double reference6MForwardFXCorrelation :
														reference6MForwardFXCorrelationArray)
													{
														for (double derived3MForwardFXCorrelation :
															derived3MForwardFXCorrelationArray)
														{
															for (double derived6MForwardFXCorrelation :
																derived6MForwardFXCorrelationArray)
															{
																for (double fundingFXCorrelation :
																	fundingFXCorrelationArray)
																{
																	VolCorrScenario (
																		new ComponentPair[]
																		{
																			mtmComponentPair,
																			nonMTMComponentPair
																		},
																		valuationParams,
																		curveSurfaceQuoteContainer,
																		reference3MForwardLabel,
																		reference6MForwardLabel,
																		derived3MForwardLabel,
																		derived6MForwardLabel,
																		fundingLabelReference,
																		fxLabel,
																		reference3MForwardVolatility,
																		reference6MForwardVolatility,
																		derived3MForwardVolatility,
																		derived6MForwardVolatility,
																		referenceFundingVolatility,
																		referenceDerivedFXVolatility,
																		reference3MForwardFundingCorrelation,
																		reference6MForwardFundingCorrelation,
																		derived3MForwardFundingCorrelation,
																		derived6MForwardFundingCorrelation,
																		reference3MForwardFXCorrelation,
																		reference6MForwardFXCorrelation,
																		derived3MForwardFXCorrelation,
																		derived6MForwardFXCorrelation,
																		fundingFXCorrelation
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
							}
						}
					}
				}
			}
		}

		EnvManager.TerminateEnv();
	}
}
