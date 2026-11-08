
package org.drip.sample.bloomberg;

import org.drip.analytics.cashflow.*;
import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
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
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * <i>SWPMOIS</i> contains the sample demonstrating the replication of Bloomberg's SWPM OIS functionality.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/bloomberg/README.md">Bloomberg CDSO CDSW SWPM YAS</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class SWPMOIS
{
	private static final String FIELD_SEPARATOR = "    ";

	private static final CalibratableComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final int[] maturityDaysArray,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibrationComponentArray =
			new CalibratableComponent[maturityDaysArray.length + futuresCount];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			calibrationComponentArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				ForwardLabel.Create (currency, maturityDaysArray[maturityIndex] + "D")
			);
		}

		CalibratableComponent[] futuresArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int futuresIndex = maturityDaysArray.length;
			futuresIndex < maturityDaysArray.length + futuresCount;
			++futuresIndex)
		{
			calibrationComponentArray[futuresIndex] = futuresArray[futuresIndex - maturityDaysArray.length];
		}

		return calibrationComponentArray;
	}

	private static final FixFloatComponent OTCIRS (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return IBORFixedFloatContainer.ConventionFromJurisdiction (
			currency,
			"ALL",
			maturityTenor,
			"MAIN"
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final CalibratableComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] tenorArray,
		final double[] couponArray)
		throws Exception
	{
		CalibratableComponent[] calibrationComponentArray = new CalibratableComponent[tenorArray.length];

		for (int tenorIndex = 0; tenorIndex < tenorArray.length; ++tenorIndex) {
			calibrationComponentArray[tenorIndex] = OTCIRS (
				effectiveDate,
				currency,
				tenorArray[tenorIndex],
				couponArray[tenorIndex]
			);
		}

		return calibrationComponentArray;
	}

	private static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency,
		final double bump)
		throws Exception
	{
		return ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
			"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
			new ValuationParams (
				spotDate,
				spotDate,
				"USD"
			),
			DepositInstrumentsFromMaturityDays (
				spotDate,
				new int[]
				{
				},
				0,
				currency
			),
			new double[]
			{
			},
			null,
			SwapInstrumentsFromMaturityTenor (
				spotDate,
				currency,
				new String[]
				{
					 "9M",
					 "1Y",
					"18M",
					 "2Y",
					 "3Y",
					 "4Y",
					 "5Y",
					"10Y"
				},
				new double[]
				{
					0.0009875,
					0.00122,
					0.00223,
					0.00383,
					0.00827,
					0.01245,
					0.01605,
					0.02597
				}
			),
			new double[]
			{
				0.0009875 + bump,   //  9M
				0.00122 + bump,     //  1Y
				0.00223 + bump,     // 18M
				0.00383 + bump,     //  2Y
				0.00827 + bump,     //  3Y
				0.01245 + bump,     //  4Y
				0.01605 + bump,     //  5Y
				0.02597 + bump      // 10Y
			},
			new String[]
			{
				"SwapRate",		//  9M
				"SwapRate",     //  1Y
				"SwapRate",     // 18M
				"SwapRate",     //  2Y
				"SwapRate",     //  3Y
				"SwapRate",     //  4Y
				"SwapRate",     //  5Y
				"SwapRate"      // 10Y
			},
			true
		);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("", true);

		JulianDate valueDate = DateUtil.Today();

		JulianDate settleDate = valueDate.addBusDays (2, "USD");

		double coupon = 0.0187;
		double fixing = 0.00087;
		double notional = 10.e+06;

		JulianDate effectiveDate = valueDate.addBusDays (2, "USD");

		JulianDate maturityDate = effectiveDate.addTenor ("5Y");

		FixFloatComponent swap = OTCIRS (effectiveDate, "USD", "5Y", 0.);

		MergedDiscountForwardCurve discountCurve = MakeDC (valueDate, "USD", 0.);

		LatentStateFixingsContainer latentStateFixingsContainer = new LatentStateFixingsContainer();

		Stream derivedStream = swap.derivedStream();

		ForwardLabel forwardLabel = derivedStream.forwardLabel();

		ComposableUnitFloatingPeriod composableUnitFloatingPeriod =
			(ComposableUnitFloatingPeriod) (derivedStream.periods().get (0).periods().get (0));

		ValuationParams valuationParams = new ValuationParams (valueDate, settleDate, "USD");

		int fixingDate = composableUnitFloatingPeriod.referenceIndexPeriod().fixingDate();

		latentStateFixingsContainer.add (fixingDate, forwardLabel, fixing);

		CaseInsensitiveTreeMap<Double> baseSwapMeasuresMap = swap.value (
			valuationParams,
			null,
			MarketParamsBuilder.Create (
				discountCurve,
				null,
				null,
				null,
				null,
				null,
				latentStateFixingsContainer
			),
			null
		);

		double basePV = baseSwapMeasuresMap.get ("PV");

		double baseFixedDV01 = baseSwapMeasuresMap.get ("FixedDV01");

		System.out.println ("\n\t||---- Valuation Details ----\n");

		System.out.println ("\t|| Trade Date  : " + valueDate);

		System.out.println ("\t|| Settle Date : " + settleDate);

		System.out.println ("\n\t||---- Swap Details ----\n");

		System.out.println ("\t|| Effective: " + effectiveDate);

		System.out.println ("\t|| Maturity:  " + maturityDate);

		System.out.println ("\n\t||---- Swap Output Measures ----\n");

		System.out.println ("\t|| Mkt Val      : " + FormatUtil.FormatDouble (basePV, 0, 0, notional));

		System.out.println (
			"\t|| Par Cpn      : " +
				FormatUtil.FormatDouble (baseSwapMeasuresMap.get ("FairPremium"), 1, 5, 100.)
		);

		System.out.println (
			"\t|| Fixed DV01   : " + FormatUtil.FormatDouble (baseFixedDV01, 0, 0, notional)
		);

		latentStateFixingsContainer.add (fixingDate, forwardLabel, fixing + 0.0001);

		CaseInsensitiveTreeMap<Double> mapSwapFixingsBumpedCalc = swap.value (
			valuationParams,
			null,
			MarketParamsBuilder.Create (
				discountCurve,
				null,
				null,
				null,
				null,
				null,
				latentStateFixingsContainer
			),
			null
		);

		double fixingsDV01 = mapSwapFixingsBumpedCalc.get ("PV") - basePV;

		System.out.println ("\t|| Fixings DV01 : " + FormatUtil.FormatDouble (fixingsDV01, 0, 0, notional));

		System.out.println (
			"\t|| Total DV01   : " + FormatUtil.FormatDouble (baseFixedDV01 + fixingsDV01, 0, 0, notional)
		);

		latentStateFixingsContainer.add (effectiveDate, forwardLabel, fixing - 0.0001);

		System.out.println (
			"\t|| PV01         : " + FormatUtil.FormatDouble (
				swap.value (
					valuationParams,
					null,
					MarketParamsBuilder.Create (
						MakeDC (valueDate, "USD", -0.0001),
						null,
						null,
						null,
						null,
						null,
						latentStateFixingsContainer
					),
					null
				).get (
					"PV"
				) - basePV,
				0,
				0,
				notional
			)
		);

		System.out.println ("\n\t||---- Fixed Cashflow ----\n");

		for (CompositePeriod period : swap.referenceStream().cashFlowPeriod()) {
			int payDate = period.payDate();

			double couponDCF = period.couponDCF();

			System.out.println (
				DateUtil.YYYYMMDD (payDate) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (period.startDate()) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (period.endDate()) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (couponDCF * 360, 0, 0, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (couponDCF, 0, 2, coupon * notional) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (discountCurve.df (payDate), 1, 4, 1.)
			);
		}

		System.out.println ("\n\t||---- Floating Cashflow ----\n");

		for (CompositePeriod period : derivedStream.cashFlowPeriod()) {
			int payDate = period.payDate();

			System.out.println (
				DateUtil.YYYYMMDD (payDate) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (period.startDate()) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (period.endDate()) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (period.couponDCF() * 360, 0, 0, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (discountCurve.df (payDate), 1, 4, 1.)
			);
		}

		EnvManager.TerminateEnv();
	}
}
