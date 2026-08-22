
package org.drip.sample.ois;

import java.util.*;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.definition.OvernightIndex;
import org.drip.market.otc.*;
import org.drip.numerical.common.*;
import org.drip.param.creator.*;
import org.drip.param.market.*;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.*;
import org.drip.state.inference.*;

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
 * <i>CrossOvernightStream</i> demonstrates the construction, customization, and valuation of Cross-Currency
 * 	Overnight Floating Streams.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/ois/README.md">Index/Fund OIS Curve Reconciliation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CrossOvernightFloatingStream
{

	private static final FixFloatComponent OTCOISFixFloat (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return OvernightFixedFloatContainer.FundConventionFromJurisdiction (
			currency
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityDaysIndex = 0; maturityDaysIndex < maturityDaysArray.length; ++maturityDaysIndex) {
			depositComponentArray[maturityDaysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityDaysIndex], currency),
				OvernightLabel.Create (currency)
			);
		}

		return depositComponentArray;
	}

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			oisArray[maturityTenorIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);
		}

		return oisArray;
	}

	private static final MergedDiscountForwardCurve CustomOISCurveBuilderSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		double[] oisQuoteArray =
		{
			0.02604,    //  4Y
			0.02808,    //  5Y
			0.02983,    //  6Y
			0.03136,    //  7Y
			0.03268,    //  8Y
			0.03383,    //  9Y
			0.03488     // 10Y
		};

		return ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			new LinearLatentStateCalibrator (
				new SegmentCustomBuilderControl (
					MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
					new PolynomialFunctionSetParams (4),
					SegmentInelasticDesignControl.Create (2, 2),
					new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
					null
				),
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"DEPOSIT",
					DepositInstrumentsFromMaturityDays (
						spotDate,
						currency,
						new int[]
						{
							1,
							2,
							3,
							7,
							14,
							21,
							30,
							60
						}
					),
					"ForwardRate",
					new double[]
					{
						0.0120,
						0.0120,
						0.0120,
						0.0145,
						0.0155,
						0.0160,
						0.0166,
						0.0185,
					}
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"EDF",
					SingleStreamComponentBuilder.ForwardRateFuturesPack (spotDate, 4, currency),
					"ForwardRate",
					new double[]
					{
						0.01612,
						0.01580,
						0.01589,
						0.01598
					}
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SWAP",
					OISFromMaturityTenor (
						spotDate,
						currency,
						new String[]
						{
							"4Y",
							"5Y",
							"6Y",
							"7Y",
							"8Y",
							"9Y",
							"10Y"
						},
						oisQuoteArray
					),
					"SwapRate",
					oisQuoteArray
				)
			},
			new ValuationParams (spotDate, spotDate, currency),
			null,
			null,
			null,
			1.
		);
	}

	private static final LatentStateFixingsContainer SetFlatOvernightFixings (
		final JulianDate startDate,
		final JulianDate endDate,
		final JulianDate valuationDate,
		final ForwardLabel forwardLabel,
		final double flatFixing,
		final double notional)
		throws Exception
	{
		LatentStateFixingsContainer latentStateFixingsContainer = new LatentStateFixingsContainer();

		int valuationDateJulian = valuationDate.julian();

		int startDateJulian = startDate.julian();

		JulianDate date = startDate.addDays (1);

		int endDateJulian = endDate.julian();

		int dateJulian = date.julian();

		double account = 1.;
		int previousDateJulian = startDateJulian;

		while (dateJulian <= endDateJulian) {
			latentStateFixingsContainer.add (date, forwardLabel, flatFixing);

			if (dateJulian <= valuationDateJulian) {
				account *= (
					1. + flatFixing * Convention.YearFraction (
						previousDateJulian,
						date.julian(),
						"Act/360",
						false,
						null,
						"USD"
					)
				);
			}

			previousDateJulian = dateJulian;

			dateJulian = (date = date.addBusDays (1, "USD")).julian();
		}

		System.out.println (
			"\t|| Manual Calc Float Accrued (Geometric Compounding): " + (account - 1.) * notional
		);

		System.out.println (
			"\t|| Manual Calc Float Accrued (Arithmetic Compounding): " +
				((valuationDateJulian - startDateJulian) * notional * flatFixing / 360.)
		);

		return latentStateFixingsContainer;
	}

	private static final Map<String, Double> CompoundingRun (
		final ForwardLabel forwardLabel)
		throws Exception
	{
		double oisVolatility = 0.3;
		double usdFundingVolatility = 0.3;
		double usdFundingUSDOISCorrelation = 0.3;

		String currency = forwardLabel.currency();

		JulianDate today = DateUtil.Today().addTenorAndAdjust ("0D", currency);

		JulianDate customOISStartDate = today.subtractTenor ("2M");

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			CustomOISCurveBuilderSample (today, currency),
			null,
			null,
			null,
			null,
			null,
			SetFlatOvernightFixings (
				customOISStartDate,
				today.addTenor ("4M"),
				today,
				forwardLabel,
				0.003,
				-1.
			)
		);

		FundingLabel fundingLabel = FundingLabel.Standard ("USD");

		int todayJulian = today.julian();

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (forwardLabel),
				forwardLabel.currency(),
				oisVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (fundingLabel),
				"USD",
				usdFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel,
			fundingLabel,
			new Flat (usdFundingUSDOISCorrelation)
		);

		return new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.OvernightEdgeDates (
					customOISStartDate,
					customOISStartDate.addTenorAndAdjust ("6M", currency),
					currency
				),
				new CompositePeriodSetting (
					360,
					"ON",
					currency,
					null,
					-1.,
					null,
					null,
					null,
					null
				),
				new ComposableFloatingUnitSetting (
					"ON",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
					null,
					forwardLabel,
					CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
					0.
				)
			)
		).value (
			new ValuationParams (today, today, currency),
			null,
			curveSurfaceQuoteContainer,
			null
		);
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

		String currency = "USD";

		Map<String, Double> arithmeticOutputMeasureMap = CompoundingRun (OvernightLabel.Create (currency));

		System.out.println ("\n\t-----------------------------------");

		System.out.println ("\t  GEOMETRIC |  ARITHMETIC | CHECK");

		System.out.println ("\t-----------------------------------\n");

		for (Map.Entry<String, Double> meGeometric : CompoundingRun (
			ForwardLabel.Create (
				new OvernightIndex (
					currency + "OIS",
					"OIS",
					currency,
					"Act/360",
					currency,
					"ON",
					0,
					CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
				),
				"ON"
			)).entrySet()
		)
		{
			String key = meGeometric.getKey();

			double geometricMeasure = meGeometric.getValue();

			double arithmeticMeasure = arithmeticOutputMeasureMap.get (key);

			System.out.println (
				"\t" + FormatUtil.FormatDouble (geometricMeasure, 1, 8, 1.) + " | " +
				FormatUtil.FormatDouble (arithmeticMeasure, 1, 8, 1.) + " | " + (
					NumberUtil.WithinTolerance (geometricMeasure, arithmeticMeasure, 1.e-08, 1.e-04) ?
					"MATCH " : "DIFFER"
				) + " <= " + key
			);
		}

		EnvManager.TerminateEnv();
	}
}
