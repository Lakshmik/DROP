
package org.drip.sample.forward;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.fra.FRAStandardComponent;
import org.drip.product.fx.ComponentPair;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioForwardCurveBuilder;
import org.drip.state.discount.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.ForwardLabel;
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
 * <i>IBORCurve</i> illustrates the Construction and Usage of the IBOR Forward Curve. It exposes the
 * 	following Functions:
 *
 *  <ul>
 * 		<li>Construct the Custom IBOR Sample Curve</li>
 * 		<li>Construct the Custom IBOR Sample Curve #2</li>
 * 		<li>Display the Forward Jacobian</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/forward/README.md">IBOR Spline Forward Curve Construction</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class IBORCurve
{

	private static final FloatFloatComponent OTCFloatFloat (
		final JulianDate spotDate,
		final String currency,
		final String derivedTenor,
		final String maturityTenor,
		final double basis)
	{
		return IBORFloatFloatContainer.ConventionFromJurisdiction (
			currency
		).createFloatFloatComponent (
			spotDate,
			derivedTenor,
			maturityTenor,
			basis,
			1.
		);
	}

	private static final FixFloatComponent OTCIRS (
		final JulianDate spotDate,
		final String currency,
		final String location,
		final String maturityTenor,
		final String index,
		final double coupon)
	{
		return IBORFixedFloatContainer.ConventionFromJurisdiction (
			currency,
			location,
			maturityTenor,
			index
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final ComponentPair OTCComponentPair (
		final JulianDate spotDate,
		final String currency,
		final String derivedTenor,
		final String maturityTenor,
		final double referenceFixedCoupon,
		final double derivedFixedCoupon,
		final double basis)
	{
		return IBORFloatFloatContainer.ConventionFromJurisdiction (
			currency
		).createFixFloatComponentPair (
			spotDate,
			derivedTenor,
			maturityTenor,
			referenceFixedCoupon,
			derivedFixedCoupon,
			basis,
			1.
		);
	}

	private static final SingleStreamComponent[] DepositFromMaturityDays (
		final JulianDate effectiveDate,
		final String[] maturityTenorArray,
		final ForwardLabel forwardLabel)
		throws Exception
	{
		if (null == maturityTenorArray || 0 == maturityTenorArray.length) {
			return null;
		}

		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityTenorArray.length];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			depositArray[tenorIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addTenor (maturityTenorArray[tenorIndex]),
				forwardLabel
			);
		}

		return depositArray;
	}

	private static final FRAStandardComponent[] FRAFromMaturityDays (
		final JulianDate effectiveDate,
		final ForwardLabel forwardLabel,
		final String[] maturityTenorArray,
		final double[] fraStrikeArray)
		throws Exception
	{
		if (null == maturityTenorArray || null == fraStrikeArray || 0 == maturityTenorArray.length) {
			return null;
		}

		FRAStandardComponent[] fraArray = new FRAStandardComponent[maturityTenorArray.length];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			fraArray[tenorIndex] = SingleStreamComponentBuilder.FRAStandard (
				effectiveDate.addTenor (maturityTenorArray[tenorIndex]),
				forwardLabel,
				fraStrikeArray[tenorIndex]
			);
		}

		return fraArray;
	}

	private static final FixFloatComponent[] FixFloatSwap2 (
		final JulianDate effectiveDate,
		final ForwardLabel forwardLabel,
		final String[] maturityTenorArray)
		throws Exception
	{
		if (null == maturityTenorArray || 0 == maturityTenorArray.length) {
			return null;
		}

		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			irsArray[tenorIndex] = OTCIRS (
				effectiveDate,
				forwardLabel.currency(),
				"ALL",
				maturityTenorArray[tenorIndex],
				"MAIN",
				0.
			);
		}

		return irsArray;
	}

	private static final FixFloatComponent[] FixFloatSwap (
		final JulianDate valueDate,
		final ForwardLabel forwardLabel,
		final String[] maturityTenorArray)
		throws Exception
	{
		if (null == maturityTenorArray || 0 == maturityTenorArray.length) {
			return null;
		}

		String currency = forwardLabel.currency();

		JulianDate effectiveDate = valueDate.addDays (2);

		int tenorInMonths = Integer.parseInt (forwardLabel.tenor().split ("M")[0]);

		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			1,
			"Act/360",
			false,
			"Act/360",
			false,
			currency,
			true,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			forwardLabel.tenor(),
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			forwardLabel,
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting floatingCompositePeriodSetting = new CompositePeriodSetting (
			12 / tenorInMonths,
			forwardLabel.tenor(),
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[tenorIndex],
				"6M"
			) ? maturityTenorArray[tenorIndex] : "6M";

			irsArray[tenorIndex] = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.BackwardEdgeDates (
							effectiveDate,
							effectiveDate.addTenor (maturityTenorArray[tenorIndex]),
							"1Y",
							null,
							CompositePeriodBuilder.SHORT_STUB
						),
						new CompositePeriodSetting (
							1,
							fixedTenor,
							currency,
							null,
							1.,
							null,
							null,
							null,
							null
						),
						fixedUnitCouponAccrualSetting,
						new ComposableFixedUnitSetting (
							fixedTenor,
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
							null,
							0.,
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							forwardLabel.tenor(),
							maturityTenorArray[tenorIndex],
							null
						),
						floatingCompositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			irsArray[tenorIndex].setPrimaryCode ("FixFloat:" + maturityTenorArray[tenorIndex]);
		}

		return irsArray;
	}

	private static final FloatFloatComponent[] FloatFloatSwap (
		final JulianDate spotDate,
		final ForwardLabel forwardLabel,
		final String[] maturityTenorArray)
		throws Exception
	{
		if (null == maturityTenorArray || 0 == maturityTenorArray.length) {
			return null;
		}

		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		int tenorInMonths = Integer.parseInt (forwardLabel.tenor().split ("M")[0]);

		String currency = forwardLabel.currency();

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			floatFloatComponentArray[tenorIndex] = OTCFloatFloat (
				spotDate,
				currency,
				tenorInMonths + "M",
				maturityTenorArray[tenorIndex],
				0.
			);
		}

		return floatFloatComponentArray;
	}

	private static final ComponentPair[] FixFloatComponentPair (
		final JulianDate spotDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel derivedForwardLabel,
		final String[] maturityTenorArray)
		throws Exception
	{
		if (null == maturityTenorArray || 0 == maturityTenorArray.length) {
			return null;
		}

		String tenor = derivedForwardLabel.tenor();

		String currency = derivedForwardLabel.currency();

		ComponentPair[] componentPairArray = new ComponentPair[maturityTenorArray.length];

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			ComponentPair componentPair = OTCComponentPair (
				spotDate,
				currency,
				tenor,
				maturityTenorArray[tenorIndex],
				0.,
				0.,
				0.
			);

			componentPairArray[tenorIndex] = OTCComponentPair (
				spotDate,
				currency,
				tenor,
				maturityTenorArray[tenorIndex],
				componentPair.referenceComponent().measureValue (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"FairPremium"
				),
				componentPair.derivedComponent().measureValue (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"FairPremium"
				),
				0.
			);
		}

		return componentPairArray;
	}

	/**
	 * Construct the Custom IBOR Sample Curve
	 * 
	 * @param discountCurve Discount Curve
	 * @param referenceForwardCurve Reference Forward Curve
	 * @param forwardLabel Floating Rate Index
	 * @param segmentCustomBuilderControl Segment Custom Builder Control
	 * @param depositTenorArray Deposit Tenor Array
	 * @param depositQuoteArray Deposit Quote Array
	 * @param depositCalibrationMeasure Deposit Calibration Measure
	 * @param fraTenorArray FRA Tenor Array
	 * @param fraQuoteArray FRA Quote Array
	 * @param fraCalibrationMeasure FRA Calibration Measure Array
	 * @param fixFloatTenorArray Fix-Float Tenor Array
	 * @param fixFloatQuoteArray Fix-Float Quote Array
	 * @param fixFloatCalibrationMeasure Fix-Float Calibration Measure
	 * @param floatFloatTenorArray Float-float Tenor Array
	 * @param floatFloatQuoteArray Float-Float Quote Array
	 * @param floatFloatCalibrationMeasure Float-float Calibration Measure
	 * @param syntheticFloatFloatTenorArray Synthetic Float-float Tenor Array
	 * @param syntheticFloatFloatQuoteArray Synthetic Float-float Quote Array
	 * @param syntheticFloatFloatCalibrationMeasure Synthetic Float-float Measure
	 * @param headerComment Header Comment
	 * @param printMetric TRUE - Print Metric
	 * 
	 * @return The Custom IBOR Sample Curve
	 * 
	 * @throws Exception Thrown if the Custom IBOR Sample Curve cannot be constructed
	 */

	public static final ForwardCurve CustomIBORBuilderSample (
		final MergedDiscountForwardCurve discountCurve,
		final ForwardCurve referenceForwardCurve,
		final ForwardLabel forwardLabel,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final String[] depositTenorArray,
		final double[] depositQuoteArray,
		final String depositCalibrationMeasure,
		final String[] fraTenorArray,
		final double[] fraQuoteArray,
		final String fraCalibrationMeasure,
		final String[] fixFloatTenorArray,
		final double[] fixFloatQuoteArray,
		final String fixFloatCalibrationMeasure,
		final String[] floatFloatTenorArray,
		final double[] floatFloatQuoteArray,
		final String floatFloatCalibrationMeasure,
		final String[] syntheticFloatFloatTenorArray,
		final double[] syntheticFloatFloatQuoteArray,
		final String syntheticFloatFloatCalibrationMeasure,
		final String headerComment,
		final boolean printMetric)
		throws Exception
	{
		if (printMetric) {
			System.out.println ("\n\t||----------------------------------------------------------------");

			System.out.println ("\t||     " + headerComment);

			System.out.println ("\t||----------------------------------------------------------------");
		}

		JulianDate valueDate = discountCurve.epoch();

		SingleStreamComponent[] depositArray = DepositFromMaturityDays (
			valueDate,
			depositTenorArray,
			forwardLabel
		);

		FRAStandardComponent[] fraArray = FRAFromMaturityDays (
			valueDate,
			forwardLabel,
			fraTenorArray,
			fraQuoteArray
		);

		FixFloatComponent[] fixFloatComponentArray = FixFloatSwap2 (
			valueDate,
			forwardLabel,
			fixFloatTenorArray
		);

		FloatFloatComponent[] floatFloatComponentArray = FloatFloatSwap (
			valueDate,
			forwardLabel,
			floatFloatTenorArray
		);

		FloatFloatComponent[] syntheticFloatFloatComponentArray = FloatFloatSwap (
			valueDate,
			forwardLabel,
			syntheticFloatFloatTenorArray
		);

		ValuationParams valuationParams = new ValuationParams (
			valueDate,
			valueDate,
			forwardLabel.currency()
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			referenceForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		ForwardCurve derivedForwardCurve = ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			new LinearLatentStateCalibrator (
				segmentCustomBuilderControl,
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardStretchSpec (
					"DEPOSIT",
					depositArray,
					depositCalibrationMeasure,
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"FRA",
					fraArray,
					fraCalibrationMeasure,
					fraQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"FIXFLOAT",
					fixFloatComponentArray,
					fixFloatCalibrationMeasure,
					fixFloatQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"FLOATFLOAT",
					floatFloatComponentArray,
					floatFloatCalibrationMeasure,
					floatFloatQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"SYNTHETICFLOATFLOAT",
					syntheticFloatFloatComponentArray,
					syntheticFloatFloatCalibrationMeasure,
					syntheticFloatFloatQuoteArray
				)
			},
			forwardLabel,
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null,
			null == depositQuoteArray || 0 == depositQuoteArray.length ?
				fraQuoteArray[0] : depositQuoteArray[0]
		);

		curveSurfaceQuoteContainer.setForwardState (derivedForwardCurve);

		if (printMetric) {
			if (null != depositArray && null != depositQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     DEPOSIT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
					System.out.println (
						"\t|| [" + depositArray[depositIndex].effectiveDate() + " - " +
							depositArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
								depositArray[depositIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									depositCalibrationMeasure
								),
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								depositQuoteArray[depositIndex],
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									depositArray[depositIndex].maturityDate()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != fraArray && null != fraQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FRA INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int fraIndex = 0; fraIndex < fraArray.length; ++fraIndex) {
					System.out.println (
						"\t|| [" + fraArray[fraIndex].effectiveDate() + " - " +
							fraArray[fraIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
								fraArray[fraIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									fraCalibrationMeasure
								),
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								fraQuoteArray[fraIndex],
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (fraArray[fraIndex].maturityDate()),
								1,
								4,
								100.
							) + "%"
						);
				}
			}

			if (null != fixFloatComponentArray && null != fixFloatQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FIX-FLOAT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int fixFloatIndex = 0; fixFloatIndex < fixFloatComponentArray.length; ++fixFloatIndex) {
					System.out.println (
						"\t|| [" + fixFloatComponentArray[fixFloatIndex].effectiveDate() + " - " +
							fixFloatComponentArray[fixFloatIndex].maturityDate() + "] =>" +
							FormatUtil.FormatDouble (
								fixFloatComponentArray[fixFloatIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									fixFloatCalibrationMeasure
								),
								1,
								4,
								100.
							) + "% | " + FormatUtil.FormatDouble (
								fixFloatQuoteArray[fixFloatIndex],
								1,
								4,
								100.
							) + "% | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									fixFloatComponentArray[fixFloatIndex].maturityDate()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != floatFloatComponentArray && null != floatFloatQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FLOAT-FLOAT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int floatFloatIndex = 0;
					floatFloatIndex < floatFloatComponentArray.length;
					++floatFloatIndex)
				{
					System.out.println (
						"\t|| [" + floatFloatComponentArray[floatFloatIndex].effectiveDate() + " - " +
							floatFloatComponentArray[floatFloatIndex].maturityDate() + "] =>" +
							FormatUtil.FormatDouble (
								floatFloatComponentArray[floatFloatIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									floatFloatCalibrationMeasure
								),
								1,
								2,
								1.
							) + " | " + FormatUtil.FormatDouble (
								floatFloatQuoteArray[floatFloatIndex],
								1,
								2,
								10000.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									floatFloatComponentArray[floatFloatIndex].maturityDate()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != syntheticFloatFloatComponentArray && null != syntheticFloatFloatQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     SYNTHETIC FLOAT-FLOAT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int syntheticFloatFloatIndex = 0;
					syntheticFloatFloatIndex < syntheticFloatFloatComponentArray.length;
					++syntheticFloatFloatIndex)
				{
					System.out.println (
						"\t|| [" +
							syntheticFloatFloatComponentArray[syntheticFloatFloatIndex].effectiveDate() +
							" - " +
							syntheticFloatFloatComponentArray[syntheticFloatFloatIndex].maturityDate() +
							"] =>" + FormatUtil.FormatDouble (
								syntheticFloatFloatComponentArray[syntheticFloatFloatIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									syntheticFloatFloatCalibrationMeasure
								),
								1,
								2,
								1.
							) + " | " + FormatUtil.FormatDouble (
								syntheticFloatFloatQuoteArray[syntheticFloatFloatIndex],
								1,
								2,
								10000.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									syntheticFloatFloatComponentArray[syntheticFloatFloatIndex].maturityDate()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}
		}

		return derivedForwardCurve;
	}

	/**
	 * Construct the Custom IBOR Sample Curve #2
	 * 
	 * @param discountCurve Discount Curve
	 * @param referenceForwardCurve Reference Forward Curve
	 * @param forwardLabel Floating Rate Index
	 * @param segmentCustomBuilderControl Segment Custom Builder Control
	 * @param depositTenorArray Deposit Tenor Array
	 * @param depositQuoteArray Deposit Quote Array
	 * @param depositCalibrationMeasure Deposit Calibration Measure
	 * @param fraTenorArray FRA Tenor Array
	 * @param fraQuoteArray FRA Quote Array
	 * @param fraCalibrationMeasure FRA Calibration Measure Array
	 * @param fixFloatTenorArray Fix-Float Tenor Array
	 * @param fixFloatQuoteArray Fix-Float Quote Array
	 * @param fixFloatCalibrationMeasure Fix-Float Calibration Measure
	 * @param componentPairTenorArray Component Pair Tenor Array
	 * @param componentPairQuoteArray Component Pair Quote Array
	 * @param componentPairCalibrationMeasure Component Pair Calibration Measure
	 * @param syntheticComponentPairTenorArray Synthetic Component Pair Tenor Array
	 * @param syntheticComponentPairQuoteArray Synthetic Component Pair Quote Array
	 * @param syntheticComponentPairCalibrationMeasure Synthetic Component Pair Measure
	 * @param headerComment Header Comment
	 * @param printMetric TRUE - Print Metric
	 * 
	 * @return The Custom IBOR Sample Curve
	 * 
	 * @throws Exception Thrown if the Custom IBOR Sample Curve cannot be constructed
	 */

	public static final ForwardCurve CustomIBORBuilderSample2 (
		final MergedDiscountForwardCurve discountCurve,
		final ForwardCurve referenceForwardCurve,
		final ForwardLabel forwardLabel,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final String[] depositTenorArray,
		final double[] depositQuoteArray,
		final String depositCalibrationMeasure,
		final String[] fraTenorArray,
		final double[] fraQuoteArray,
		final String fraCalibrationMeasure,
		final String[] fixFloatTenorArray,
		final double[] fixFloatQuoteArray,
		final String fixFloatCalibrationMeasure,
		final String[] componentPairTenorArray,
		final double[] componentPairQuoteArray,
		final String componentPairCalibrationMeasure,
		final String[] syntheticComponentPairTenorArray,
		final double[] syntheticComponentPairQuoteArray,
		final String syntheticComponentPairCalibrationMeasure,
		final String headerComment,
		final boolean printMetric)
		throws Exception
	{
		if (printMetric) {
			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     " + headerComment);

			System.out.println ("\t----------------------------------------------------------------");
		}

		JulianDate valueDate = discountCurve.epoch();

		ValuationParams valuationParams = new ValuationParams (
			valueDate,
			valueDate,
			forwardLabel.currency()
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			referenceForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		SingleStreamComponent[] depositArray = DepositFromMaturityDays (
			valueDate,
			depositTenorArray,
			forwardLabel
		);

		FRAStandardComponent[] fraArray = FRAFromMaturityDays (
			valueDate,
			forwardLabel,
			fraTenorArray,
			fraQuoteArray
		);

		FixFloatComponent[] fixFloatComponentArray = FixFloatSwap (
			valueDate,
			forwardLabel,
			fixFloatTenorArray
		);

		ComponentPair[] componentPairArray = FixFloatComponentPair (
			valueDate,
			curveSurfaceQuoteContainer,
			forwardLabel,
			componentPairTenorArray
		);

		ComponentPair[] syntheticComponentPairArray = FixFloatComponentPair (
			valueDate,
			curveSurfaceQuoteContainer,
			forwardLabel,
			syntheticComponentPairTenorArray
		);

		ForwardCurve derivedForwardCurve = ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			new LinearLatentStateCalibrator (
				segmentCustomBuilderControl,
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardStretchSpec (
					"DEPOSIT",
					depositArray,
					depositCalibrationMeasure,
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"FRA",
					fraArray,
					fraCalibrationMeasure,
					fraQuoteArray
				),
				LatentStateStretchBuilder.ForwardStretchSpec (
					"FIXFLOAT",
					fixFloatComponentArray,
					fixFloatCalibrationMeasure,
					fixFloatQuoteArray
				),
				LatentStateStretchBuilder.ComponentPairForwardStretch (
					"FIXFLOATCP",
					componentPairArray,
					valuationParams,
					curveSurfaceQuoteContainer,
					componentPairQuoteArray,
					true,
					true
				),
				LatentStateStretchBuilder.ComponentPairForwardStretch (
					"SYNTHETICFIXFLOATCP",
					syntheticComponentPairArray,
					valuationParams,
					curveSurfaceQuoteContainer,
					syntheticComponentPairQuoteArray,
					true,
					true
				)
			},
			forwardLabel,
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null,
			null == depositQuoteArray || 0 == depositQuoteArray.length ? fraQuoteArray[0] : depositQuoteArray[0]
		);

		curveSurfaceQuoteContainer.setForwardState (derivedForwardCurve);

		if (printMetric) {
			if (null != depositArray && null != depositQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     DEPOSIT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
					System.out.println (
						"\t|| [" + depositArray[depositIndex].effectiveDate() + " - " +
							depositArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
								depositArray[depositIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									depositCalibrationMeasure
								),
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								depositQuoteArray[depositIndex],
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (depositArray[depositIndex].maturityDate()),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != fraArray && null != fraQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FRA INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int fraIndex = 0; fraIndex < fraArray.length; ++fraIndex) {
					System.out.println (
						"\t|| [" + fraArray[fraIndex].effectiveDate() + " - " +
							fraArray[fraIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
								fraArray[fraIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									fraCalibrationMeasure
								),
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								fraQuoteArray[fraIndex],
								1,
								6,
								1.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (fraArray[fraIndex].maturityDate()),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != fixFloatComponentArray && null != fixFloatQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FIX-FLOAT INSTRUMENTS QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int fixFloatIndex = 0; fixFloatIndex < fixFloatComponentArray.length; ++fixFloatIndex) {
					System.out.println (
						"\t|| [" + fixFloatComponentArray[fixFloatIndex].effectiveDate() + " - " +
							fixFloatComponentArray[fixFloatIndex].maturityDate() + "] =>" +
							FormatUtil.FormatDouble (
								fixFloatComponentArray[fixFloatIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									fixFloatCalibrationMeasure
								),
								1,
								2,
								100.
							) + "% | " + FormatUtil.FormatDouble (
								fixFloatQuoteArray[fixFloatIndex],
								1,
								2,
								100.
							) + "% | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									fixFloatComponentArray[fixFloatIndex].maturityDate()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != componentPairArray && null != componentPairQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     FIX-FLOAT COMPONENT PAIR QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int componentPairIndex = 0;
					componentPairIndex < componentPairArray.length;
					++componentPairIndex)
				{
					System.out.println (
						"\t|| [" + componentPairArray[componentPairIndex].effective() + " - " +
							componentPairArray[componentPairIndex].maturity() + "] =>" +
							FormatUtil.FormatDouble (
								componentPairArray[componentPairIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									componentPairCalibrationMeasure
								),
								1,
								2,
								1.
							) + " | " + FormatUtil.FormatDouble (
								componentPairQuoteArray[componentPairIndex],
								1,
								2,
								10000.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									componentPairArray[componentPairIndex].maturity()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}

			if (null != syntheticComponentPairArray && null != syntheticComponentPairQuoteArray) {
				System.out.println ("\t||----------------------------------------------------------------");

				System.out.println ("\t||     SYNTHETIC FIX-FLOAT COMPONENT PAIR QUOTE RECOVERY");

				System.out.println ("\t||----------------------------------------------------------------");

				for (int syntheticComponentPairIndex = 0;
					syntheticComponentPairIndex < syntheticComponentPairArray.length;
					++syntheticComponentPairIndex)
				{
					System.out.println (
						"\t|| [" + syntheticComponentPairArray[syntheticComponentPairIndex].effective() +
							" - " + syntheticComponentPairArray[syntheticComponentPairIndex].maturity() +
							"] =>" + FormatUtil.FormatDouble (
								syntheticComponentPairArray[syntheticComponentPairIndex].measureValue (
									valuationParams,
									null,
									curveSurfaceQuoteContainer,
									null,
									syntheticComponentPairCalibrationMeasure
								),
								1,
								2,
								1.
							) + " | " + FormatUtil.FormatDouble (
								syntheticComponentPairQuoteArray[syntheticComponentPairIndex],
								1,
								2,
								10000.
							) + " | " + FormatUtil.FormatDouble (
								derivedForwardCurve.forward (
									syntheticComponentPairArray[syntheticComponentPairIndex].maturity()
								),
								1,
								4,
								100.
							) + "%"
					);
				}
			}
		}

		return derivedForwardCurve;
	}

	private static final void ForwardJack (
		final JulianDate date,
		final ForwardCurve forwardCurve,
		final String startDateTenor,
		final String manifestMeasure)
	{
		JulianDate jacobianDate = date.addTenor (startDateTenor);

		System.out.println (
			"\t|| " +  jacobianDate + " | " + startDateTenor + ": " +
			forwardCurve.jackDForwardDManifestMeasure (manifestMeasure, jacobianDate).displayString()
		);
	}

	/**
	 * Display the Forward Jacobian
	 * 
	 * @param date Date
	 * @param headerComment Header Comment
	 * @param forwardCurve Forward Curve
	 * @param manifestMeasure Manifest Measure
	 */

	public static final void ForwardJack (
		final JulianDate date,
		final String headerComment,
		final ForwardCurve forwardCurve,
		final String manifestMeasure)
	{
		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||" + headerComment);

		System.out.println ("\t||----------------------------------------------------------------");

		ForwardJack (date, forwardCurve, "1Y", manifestMeasure);

		ForwardJack (date, forwardCurve, "2Y", manifestMeasure);

		ForwardJack (date, forwardCurve, "3Y", manifestMeasure);

		ForwardJack (date, forwardCurve, "5Y", manifestMeasure);

		ForwardJack (date, forwardCurve, "7Y", manifestMeasure);
	}
}
