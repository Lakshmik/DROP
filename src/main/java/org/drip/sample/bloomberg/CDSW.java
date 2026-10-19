
package org.drip.sample.bloomberg;

/*
 * Credit Products imports
 */

import org.drip.analytics.cashflow.*;
import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.pricer.CreditPricerParams;
import org.drip.param.valuation.*;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.product.creator.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.credit.CreditCurve;
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
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>CDSW</i> contains the sample demonstrating the replication of Bloomberg's CDSW functionality.
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

public class CDSW
{
	private static final String FIELD_SEPARATOR = "   ";

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

	private static MergedDiscountForwardCurve BuildRatesCurveFromInstruments (
		final JulianDate startDate,
		final String[] cashTenorArray,
		final double[] cashRateArray,
		final String[] irsTenorArray,
		final double[] irsRateArray,
		final double bump,
		final String currency)
		throws Exception
	{
		int instrumentCount = cashTenorArray.length + irsRateArray.length;
		int[] dateArray = new int[instrumentCount];
		double[] rateArray = new double[instrumentCount];
		String[] calibrationMeasureArray = new String[instrumentCount];
		double[] calibratableComponentValueArray = new double[instrumentCount];
		CalibratableComponent[] calibratableComponentArray = new CalibratableComponent[instrumentCount];

		JulianDate effectiveCashDate = startDate.addBusDays (1, currency);

		for (int cashIndex = 0; cashIndex < cashTenorArray.length; ++cashIndex) {
			rateArray[cashIndex] = Double.NaN;
			calibrationMeasureArray[cashIndex] = "Rate";
			calibratableComponentValueArray[cashIndex] = cashRateArray[cashIndex] + bump;

			calibratableComponentArray[cashIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveCashDate,
				new JulianDate (
					dateArray[cashIndex] = effectiveCashDate.addTenor (cashTenorArray[cashIndex]).julian()
				),
				ForwardLabel.Create (
					currency,
					cashTenorArray[cashIndex]
				)
			);
		}

		JulianDate effectiveIRSDate = startDate.addBusDays (2, currency);

		for (int irsIndex = 0; irsIndex < irsTenorArray.length; ++irsIndex) {
			rateArray[irsIndex + cashTenorArray.length] = Double.NaN;
			calibrationMeasureArray[irsIndex + cashTenorArray.length] = "Rate";
			calibratableComponentValueArray[irsIndex + cashTenorArray.length] =
				irsRateArray[irsIndex] + bump;

			dateArray[irsIndex + cashTenorArray.length] =
				effectiveIRSDate.addTenor (irsTenorArray[irsIndex]).julian();

			calibratableComponentArray[irsIndex + cashTenorArray.length] = OTCIRS (
				effectiveIRSDate,
				currency,
				irsTenorArray[irsIndex],
				0.
			);
		}

		return ScenarioDiscountCurveBuilder.NonlinearBuild (
			startDate,
			currency,
			calibratableComponentArray,
			calibratableComponentValueArray,
			calibrationMeasureArray,
			null
		);
	}

	private static CreditCurve CreateCreditCurveFromCDS (
		final JulianDate startDate,
		final double[] quoteArray,
		final String[] tenorArray,
		final String measure,
		final MergedDiscountForwardCurve discountCurve,
		final double recoveryRate,
		final String creditCurveName,
		final double strike,
		final double bump)
		throws Exception
	{
		String[] calibrationMeasureArray = new String[quoteArray.length];
		CreditDefaultSwap[] creditDefaultSwapArray = new CreditDefaultSwap[quoteArray.length];

		for (int tenorIndex = 0; tenorIndex < tenorArray.length; ++tenorIndex) {
			creditDefaultSwapArray[tenorIndex] = CDSBuilder.CreateSNAC (
				startDate,
				tenorArray[tenorIndex],
				strike,
				creditCurveName
			);

			calibrationMeasureArray[tenorIndex] = measure;
			quoteArray[tenorIndex] += bump;
		}

		return ScenarioCreditCurveBuilder.Custom (
			creditCurveName,
			startDate,
			creditDefaultSwapArray,
			discountCurve,
			quoteArray,
			calibrationMeasureArray,
			recoveryRate,
			"QuotedSpread".equals (measure)
		);
	}

	private static final void DisplayInstrumentMaturitySurvival (
		final CreditCurve creditCurve)
		throws Exception
	{
		CalibratableComponent[] creditDefaultSwapArray = creditCurve.calibComp();

		for (int cdsIndex = 0; cdsIndex < creditDefaultSwapArray.length; ++cdsIndex) {
			System.out.println (
				creditDefaultSwapArray[cdsIndex].maturityDate() + " | " + creditCurve.manifestMeasure (
					creditDefaultSwapArray[cdsIndex].primaryCode()
				) + " | " + FormatUtil.FormatDouble (
					1. - creditCurve.survival (creditDefaultSwapArray[cdsIndex].maturityDate()),
					1,
					3,
					1.
				)
			);
		}
	}

	private static CreditDefaultSwap CreateCDS (
		final JulianDate startDate,
		final String tenor,
		final double coupon,
		final String creditCurveName)
	{
		return CDSBuilder.CreateSNAC (startDate, tenor, coupon, creditCurveName);
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

		JulianDate curveDate = DateUtil.CreateFromYMD (2018, DateUtil.JANUARY, 5);

		JulianDate valueDate = curveDate.addDays (1);

		JulianDate settleDate = valueDate.addBusDays (3, "USD");

		double recoveryRate = 0.4;
		double notional = -10.e+06;
		String[] cashTenorArray =
		{
			"1M",
			"2M",
			"3M",
			"6M",
			"12M"
		};
		double[] cashRateArray =
		{
			0.001864,
			0.002289,
			0.002638,
			0.003965,
			0.006759
		};
		String[] irsTenorArray =
		{
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "6Y",
			 "7Y",
			 "8Y",
			 "9Y",
			"10Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y"
		};
		double[] irsRateArray =
		{
			0.004750,
			0.007700,
			0.011600,
			0.015425,
			0.018900,
			0.021760,
			0.024105,
			0.026095,
			0.027750,
			0.030400,
			0.032890,
			0.034855,
			0.035805,
			0.036345
		};
		String[] cdsTenorArray =
		{
			 "6M",
			 "1Y",
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			"10Y"
		};
		double[] cdsParSpreadArray =
		{
			 60.,
			 68.,
			 88.,
			102.,
			121.,
			138.,
			168.,
			188.
		};

		MergedDiscountForwardCurve discountCount = BuildRatesCurveFromInstruments (
			curveDate,
			cashTenorArray,
			cashRateArray,
			irsTenorArray,
			irsRateArray,
			0.,
			"USD"
		);

		CreditCurve creditCurve = CreateCreditCurveFromCDS (
			valueDate,
			cdsParSpreadArray,
			cdsTenorArray,
			"FairPremium",
			discountCount,
			recoveryRate,
			"MS",
			0.01,
			0.
		);

		DisplayInstrumentMaturitySurvival (creditCurve);

		CreditDefaultSwap cds = CreateCDS (valueDate, "6Y", 0.05, "MS");

		ValuationParams valuationParams = new ValuationParams (valueDate, settleDate, "USD");

		CreditPricerParams pricerParams = CreditPricerParams.Standard();

		System.out.println ("\n\t||---- Valuation Details ----");

		System.out.println ("\t|| Trade Date   : " + curveDate);

		System.out.println ("\t|| Cash Settle  : " + settleDate);

		System.out.println ("\n\t||---- CDS Details ----");

		System.out.println ("\t|| Effective    : " + cds.effectiveDate());

		System.out.println ("\t|| Maturity     : " + cds.maturityDate());

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Credit (discountCount, creditCurve);

		CaseInsensitiveTreeMap<Double> mapBaseMeasures =
			cds.value (valuationParams, pricerParams, curveSurfaceQuoteContainer, null);

		double accrued = mapBaseMeasures.get ("Accrued") * 100. * notional;

		double baseDirtyPV = mapBaseMeasures.get ("DirtyPV");

		double principal = mapBaseMeasures.get ("Upfront") * 0.01 * notional;

		System.out.println ("\n\t||---- Base CDS Measures ----");

		System.out.println (
			"\t|| Price        : " + FormatUtil.FormatDouble (mapBaseMeasures.get ("Price"), 1, 2, 1.)
		);

		System.out.println ("\t|| Principal    : " + FormatUtil.FormatDouble (principal, 1, 0, 1.));

		System.out.println ("\t|| Accrued      : " + FormatUtil.FormatDouble (accrued, 1, 0, 1.));

		System.out.println (
			"\t|| Accrual Days : " + FormatUtil.FormatDouble (mapBaseMeasures.get ("AccrualDays"), 1, 0, 1.)
		);

		System.out.println (
			"\t|| Cash Amount  : " + FormatUtil.FormatDouble (accrued + principal, 1, 0, 1.)
		);

		System.out.println (
			"\t|| Repl Spread  : " + FormatUtil.FormatDouble (mapBaseMeasures.get ("FairPremium"), 1, 4, 1.)
		);

		System.out.println (
			"\t|| CS01         : " + FormatUtil.FormatDouble (
				cds.value (
					valuationParams,
					pricerParams,
					MarketParamsBuilder.Credit (
						discountCount,
						CreateCreditCurveFromCDS (
							valueDate,
							cdsParSpreadArray,
							cdsTenorArray,
							"FairPremium",
							discountCount,
							recoveryRate,
							"MS",
							0.01,
							1.
						)
					),
					null
				).get (
					"DirtyPV"
				) - baseDirtyPV,
				1,
				0,
				0.01 * notional
			)
		);

		System.out.println (
			"\t|| IR01         : " + FormatUtil.FormatDouble (
				cds.value (
					valuationParams,
					pricerParams,
					MarketParamsBuilder.Credit (
						BuildRatesCurveFromInstruments (
							curveDate,
							cashTenorArray,
							cashRateArray,
							irsTenorArray,
							irsRateArray,
							0.0001,
							"USD"
						),
						creditCurve
					),
					null
				).get (
					"DirtyPV"
				) - baseDirtyPV,
				1,
				0,
				0.01 * notional
			)
		);

		System.out.println ("\n\t||---- CDS Coupon Flows ----");

		for (CompositePeriod compositePeriod : cds.couponPeriods()) {
			int payDateJulian = compositePeriod.payDate();

			double couponDCF = compositePeriod.couponDCF();

			System.out.println (
				"\t|| " + DateUtil.YYYYMMDD (compositePeriod.startDate()) + FIELD_SEPARATOR +
					DateUtil.YYYYMMDD (compositePeriod.endDate()) + FIELD_SEPARATOR +
					DateUtil.YYYYMMDD (payDateJulian) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (couponDCF, 1, 3, 1.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (couponDCF, 1, 2, 0.01 * notional) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (discountCount.df (payDateJulian), 1, 4, 1.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (creditCurve.survival (payDateJulian), 1, 4, 1.)
			);
		}

		CaseInsensitiveTreeMap<Double> quotedSpreadMeasureMap = cds.valueFromQuotedSpread (
			valuationParams,
			pricerParams,
			curveSurfaceQuoteContainer,
			null,
			0.05,
			208.
		);

		System.out.println ("\n\t||---- Quoted Spread CDS Measures ----");

		System.out.println (
			"\t|| QS Price     : " + FormatUtil.FormatDouble (quotedSpreadMeasureMap.get ("Price"), 1, 2, 1.)
		);

		System.out.println (
			"\t|| QS Repl Spd  : " +
				FormatUtil.FormatDouble (quotedSpreadMeasureMap.get ("FairPremium"), 1, 4, 1.)
		);

		EnvManager.TerminateEnv();
	}
}
