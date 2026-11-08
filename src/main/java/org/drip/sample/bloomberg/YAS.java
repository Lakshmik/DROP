
package org.drip.sample.bloomberg;

/*
 * Credit Product imports
 */

import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.*;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.definition.*;
import org.drip.param.valuation.*;
import org.drip.product.definition.*;
import org.drip.product.govvie.TreasuryComponent;
import org.drip.product.params.EmbeddedOptionSchedule;

/*
 * Credit Analytics API imports
 */

import org.drip.product.rates.*;
import org.drip.param.creator.*;
import org.drip.param.market.*;
import org.drip.param.quote.*;
import org.drip.product.creator.*;
import org.drip.product.credit.BondComponent;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.TreasuryBuilder;
import org.drip.state.creator.*;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.govvie.GovvieCurve;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>YAS</i> contains the sample demonstrating the replication of Bloomberg's YAS functionality.
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

public class YAS
{
	private static final String FIELD_SEPARATOR = "    ";

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
		int dateArray[] = new int[instrumentCount];
		double rateArray[] = new double[instrumentCount];
		double calibrationValueArray[] = new double[instrumentCount];
		String calibrationMeasureArray[] = new String[instrumentCount];
		CalibratableComponent calibratableComponentArray[] = new CalibratableComponent[instrumentCount];

		JulianDate effectiveCashDate = startDate.addBusDays (1, currency);

		for (int cashIndex = 0; cashIndex < cashTenorArray.length; ++cashIndex) {
			rateArray[cashIndex] = Double.NaN;
			calibrationMeasureArray[cashIndex] = "Rate";
			calibrationValueArray[cashIndex] = cashRateArray[cashIndex] + bump;

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
			calibrationMeasureArray[irsIndex + cashTenorArray.length] = "SwapRate";
			calibrationValueArray[irsIndex + cashTenorArray.length] = irsRateArray[irsIndex] + bump;

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
			calibrationValueArray,
			calibrationMeasureArray,
			null
		);
	}

	private static final TreasuryComponent Treasury (
		final String name,
		final double coupon,
		final JulianDate date,
		final String tenor)
		throws Exception
	{
		return TreasuryBuilder.FromCode ("UST", date, date.addTenor (tenor), coupon);
	}

	private static final TreasuryComponent[] OTRTreasurySet (
		final JulianDate date,
		final String[] tenorArray,
		final double[] couponArray)
		throws Exception
	{
		TreasuryComponent[] treasuryArray = new TreasuryComponent[tenorArray.length];

		for (int tenorIndex = 0; tenorIndex < tenorArray.length; ++tenorIndex) {
			treasuryArray[tenorIndex] = Treasury (
				"TSY" + tenorArray[tenorIndex] + "ON",
				couponArray[tenorIndex],
				date,
				tenorArray[tenorIndex]
			);
		}

		return treasuryArray;
	}

	private static final GovvieCurve BuildOnTheRunGovvieCurve (
		final JulianDate date,
		final TreasuryComponent[] treasuryArray,
		final double[] yieldArray)
		throws Exception
	{
		return ScenarioGovvieCurveBuilder.CubicPolyShapePreserver (
			"UST",
			"UST",
			treasuryArray[0].currency(),
			date.julian(),
			treasuryArray,
			yieldArray,
			"Yield"
		);
	}

	private static final CaseInsensitiveTreeMap<ProductQuote> MakeTSYQuotes (
		final String[] treasuryTenorArray,
		final double[] treasuryYieldArray)
		throws Exception
	{
		CaseInsensitiveTreeMap<ProductQuote> treasuryQuoteMap = new CaseInsensitiveTreeMap<ProductQuote>();

		for (int treasuryIndex = 0; treasuryIndex < treasuryTenorArray.length; ++treasuryIndex) {
			ProductMultiMeasure productMultiMeasureQuote = new ProductMultiMeasure();

			productMultiMeasureQuote.addQuote (
				"Yield",
				new MultiSided ("mid", treasuryYieldArray[treasuryIndex], Double.NaN),
				true
			);

			treasuryQuoteMap.put (treasuryTenorArray[treasuryIndex] + "ON", productMultiMeasureQuote);
		}

		return treasuryQuoteMap;
	}

	private static final void BondPricerSample()
		throws Exception
	{
		JulianDate curveDate = DateUtil.CreateFromYMD (2013, 4, 21);

		JulianDate settleDate = curveDate.addBusDays (3, "USD");

		double price = 0.97828;
		double notional = 1000000.;
		String[] cashTenorArray =
		{
			"3M"
		};
		double[] cashRateArray =
		{
			0.00276
		};
		String[] irsTenorArray =
		{
			 "1Y",
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "6Y",
			 "7Y",
			 "8Y",
			 "9Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};
		double[] irsRateArray =
		{
			0.00367,
			0.00533,
			0.00843,
			0.01238,
			0.01609,
			0.01926,
			0.02191,
			0.02406,
			0.02588,
			0.02741,
			0.02870,
			0.02982,
			0.03208,
			0.03372,
			0.03445,
			0.03484,
			0.03501,
			0.03484
		};
		String[] treasuryTenorArray =
		{
			 "1Y",
			 "2Y",
			 "3Y",
			 "5Y",
			 "7Y",
			"10Y",
			"30Y"
		};
		final double[] treasuryCouponArray =
		{
			0.00000,
			0.00375,
			0.00500,
			0.01000,
			0.01375,
			0.01375,
			0.02875
		};
		double[] treasuryYieldArray =
		{
			0.00160,
			0.00397,
			0.00696,
			0.01421,
			0.01955,
			0.02529,
			0.03568
		};
		int[] exerciseDateArray =
		{
			DateUtil.CreateFromYMD (2016, 3, 1).julian(),
			DateUtil.CreateFromYMD (2017, 3, 1).julian(),
			DateUtil.CreateFromYMD (2018, 3, 1).julian(),
			DateUtil.CreateFromYMD (2019, 3, 1).julian(),
			DateUtil.CreateFromYMD (2020, 3, 1).julian()
		};
		double[] factorArray =
		{
			1.045,
			1.030,
			1.015,
			1.000,
			1.000
		};

		MergedDiscountForwardCurve discountCurve = BuildRatesCurveFromInstruments (
			curveDate,
			cashTenorArray,
			cashRateArray,
			irsTenorArray,
			irsRateArray,
			0.,
			"USD"
		);

		BondComponent bond = BondBuilder.CreateSimpleFixed (
			"TEST",									// Name
			"USD",									// Currency
			"",										// Empty Credit Curve
			0.054,									// Bond Coupon
			2, 										// Frequency
			"30/360",								// Day Count
			DateUtil.CreateFromYMD (2011, 4, 21), 	// Effective
			DateUtil.CreateFromYMD (2021, 4, 15),	// Maturity
			null,									// Principal Schedule
			null
		);

		bond.setEmbeddedCallSchedule (
			new EmbeddedOptionSchedule (
				exerciseDateArray,
				factorArray,
				false,
				30,
				false,
				Double.NaN,
				"",
				Double.NaN
			)
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			BuildOnTheRunGovvieCurve (
				curveDate,
				OTRTreasurySet (curveDate, treasuryTenorArray, treasuryCouponArray),
				treasuryYieldArray
			),
			null,
			null,
			null, 
			MakeTSYQuotes (
				treasuryTenorArray,
				treasuryYieldArray
			),
			null
		);

		ValuationParams valuationParams = ValuationParams.Spot (
			settleDate,
			0,
			"",
			Convention.DATE_ROLL_ACTUAL
		);

		WorkoutInfo workoutInfo =
			bond.exerciseYieldFromPrice (valuationParams, curveSurfaceQuoteContainer, null, price);

		double accrued = bond.accrued (valuationParams.valueDate(), curveSurfaceQuoteContainer);

		double workoutFactor = workoutInfo.factor();

		int workoutDate = workoutInfo.date();

		double yield01 = bond.yield01FromPrice (
			valuationParams,
			curveSurfaceQuoteContainer,
			null,
			workoutDate,
			workoutFactor,
			price
		);

		System.out.println ("\n\t||---- Valuation Details ----");

		System.out.println ("\t|| Trade Date   : " + curveDate);

		System.out.println ("\t|| Cash Settle  : " + settleDate);

		System.out.println ("\n\t||--------");

		System.out.println ("\t|| Price          : " + FormatUtil.FormatDouble (price, 1, 3, 100.));

		System.out.println (
			"\t|| Yield          : " + FormatUtil.FormatDouble (workoutInfo.yield(), 1, 3, 100.)
		);

		System.out.println ("\t|| Workout Date   : " + new JulianDate (workoutDate));

		System.out.println ("\t|| Workout Factor : " + FormatUtil.FormatDouble (workoutFactor, 1, 2, 100.));

		System.out.println ("\n\t||--SPREAD AND YIELD CALCULATIONS--\n");

		System.out.println (
			"\t|| TSY Spread : " + FormatUtil.FormatDouble (
				bond.tsySpreadFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println (
			"\t|| G Spread   : " + FormatUtil.FormatDouble (
				bond.gSpreadFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println (
			"\t|| I Spread   : " + FormatUtil.FormatDouble (
				bond.iSpreadFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println (
			"\t|| Z Spread   : " + FormatUtil.FormatDouble (
				bond.zSpreadFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println (
			"\t|| ASW        : " + FormatUtil.FormatDouble (
				bond.aswFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println (
			"\t|| OAS        : " + FormatUtil.FormatDouble (
				bond.oasFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				0,
				10000.
			)
		);

		System.out.println ("\nt||--RISK--\n");

		System.out.println (
			"\t|| Modified Duration : " + FormatUtil.FormatDouble (
				bond.modifiedDurationFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				2,
				10000.
			)
		);

		System.out.println (
			"\t|| Macaulay Duration : " + FormatUtil.FormatDouble (
				bond.macaulayDurationFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t|| Risk              : " + FormatUtil.FormatDouble (yield01 * 10000., 1, 2, 1.)
		);

		System.out.println (
			"\t|| Convexity         : " + FormatUtil.FormatDouble (
				bond.convexityFromPrice (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					workoutDate,
					workoutFactor,
					price
				),
				1,
				2,
				1000000.
			)
		);

		System.out.println (
			"\t|| DV01              : " + FormatUtil.FormatDouble (yield01 * notional, 1, 0, 1.)
		);

		System.out.println ("\n\t|| --INVOICE--\n");

		System.out.println ("\t|| Face      : " + FormatUtil.FormatDouble (notional, 1, 0, 1.));

		System.out.println ("\t|| Principal : " + FormatUtil.FormatDouble (price * notional, 1, 2, 1.));

		System.out.println ("\t|| Accrued   : " + FormatUtil.FormatDouble (accrued * notional, 1, 2, 1.));

		System.out.println (
			"\t|| Total     : " + FormatUtil.FormatDouble ((price + accrued) * notional, 1, 2, 1.)
		);

		System.out.println ("\n\t|| Cashflow\n--------");

		for (CompositePeriod period : bond.couponPeriods()) {
			int payDate = period.payDate();

			System.out.println (
				DateUtil.YYYYMMDD (period.startDate()) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (period.endDate()) + FIELD_SEPARATOR +
				DateUtil.YYYYMMDD (payDate) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (period.couponDCF(), 1, 4, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (discountCurve.df (payDate), 1, 4, 1.) + FIELD_SEPARATOR
			);
		}
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
		EnvManager.InitEnv ("", true);

		BondPricerSample();

		EnvManager.TerminateEnv();
	}
}
