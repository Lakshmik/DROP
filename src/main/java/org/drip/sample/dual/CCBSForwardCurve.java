
package org.drip.sample.dual;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.fx.*;
import org.drip.product.params.*;
import org.drip.product.rates.*;
import org.drip.sample.forward.IBORCurve;
import org.drip.service.common.FormatUtil;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.stretch.*;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.estimator.*;
import org.drip.state.forward.ForwardCurve;
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
 * <i>CCBSForwardCurve</i> demonstrates the setup and construction of the Forward Curve from the CCBS Quotes.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/capfloor/README.md">FRA Standard Cap Floor Valuation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CCBSForwardCurve
{

	private static final FloatFloatComponent[] MakexM6MBasisSwap (
		final JulianDate effectiveDate,
		final String payCurrency,
		final String couponCurrency,
		final double notional,
		final String[] maturityTenorArray,
		final int tenorInMonths)
		throws Exception
	{
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		ComposableFloatingUnitSetting referenceComposableFloatingUnitSetting =
			new ComposableFloatingUnitSetting (
				"6M",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				ForwardLabel.Create (couponCurrency, "6M"),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

		ComposableFloatingUnitSetting derivedComposableFloatingUnitSetting =
			new ComposableFloatingUnitSetting (
				tenorInMonths + "M",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				ForwardLabel.Create (couponCurrency, tenorInMonths + "M"),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

		CompositePeriodSetting referenceCompositePeriodSetting = new CompositePeriodSetting (
			2,
			"6M",
			payCurrency,
			null,
			-1. * notional,
			null,
			null,
			payCurrency.equalsIgnoreCase (couponCurrency) ? null :
				new FixingSetting (
					FixingSetting.FIXING_PRESET_STATIC,
					null,
					effectiveDate.julian()
				),
			null
		);

		CompositePeriodSetting derivedCompositePeriodSetting = new CompositePeriodSetting (
			12 / tenorInMonths,
			tenorInMonths + "M",
			payCurrency,
			null,
			1. * notional,
			null,
			null,
			payCurrency.equalsIgnoreCase (couponCurrency) ?
				null : new FixingSetting (FixingSetting.FIXING_PRESET_STATIC, null, effectiveDate.julian()),
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, payCurrency, 0);

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			Stream referenceStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"6M",
						maturityTenorArray[tenorIndex],
						null
					),
					referenceCompositePeriodSetting,
					referenceComposableFloatingUnitSetting
				)
			);

			Stream derivedStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						tenorInMonths + "M",
						maturityTenorArray[tenorIndex],
						null
					),
					derivedCompositePeriodSetting,
					derivedComposableFloatingUnitSetting
				)
			);

			floatFloatComponentArray[tenorIndex] = new FloatFloatComponent (
				referenceStream,
				derivedStream,
				cashSettleParams
			);

			floatFloatComponentArray[tenorIndex].setPrimaryCode (
				referenceStream.name() + "||" + derivedStream.name()
			);
		}

		return floatFloatComponentArray;
	}

	private static final ComponentPair[] MakeCCSP (
		final JulianDate valueDate,
		final String referenceCurrency,
		final String derivedCurrency,
		final String[] tenorArray,
		final int tenorInMonths,
		final double referenceToDerivedFX)
		throws Exception
	{
		FloatFloatComponent[] referenceFloatFloatComponent = MakexM6MBasisSwap (
			valueDate,
			derivedCurrency,
			referenceCurrency,
			-1.,
			tenorArray,
			3
		);

		FloatFloatComponent[] derivedFloatFloatComponent = MakexM6MBasisSwap (
			valueDate,
			derivedCurrency,
			derivedCurrency,
			1. / referenceToDerivedFX,
			tenorArray,
			3
		);

		ComponentPair[] componentPairArray = new ComponentPair[tenorArray.length];

		for (int componentPairIndex = 0;
			componentPairIndex < componentPairArray.length;
			++componentPairIndex)
		{
			componentPairArray[componentPairIndex] = new ComponentPair (
				derivedCurrency + referenceCurrency + "_" + tenorArray[componentPairIndex],
				referenceFloatFloatComponent[componentPairIndex],
				derivedFloatFloatComponent[componentPairIndex],
				null
			);
		}

		return componentPairArray;
	}

	/**
	 * Set the Forward Curve Reference Component Basis
	 * 
	 * @param referenceCurrency Reference Currency
	 * @param derivedCurrency Derived Currency
	 * @param valueDate Valuation Date
	 * @param referenceDiscountCurve Reference Discount Curve
	 * @param reference6MForwardCurve 6M Reference Forward Curve
	 * @param reference3MForwardCurve 3M Reference Forward Curve
	 * @param derivedDiscountCurve Derived Discount Curve
	 * @param derived6MForwardCurve 6M Derived Forward Curve
	 * @param referenceToDerivedFX Reference/Forward FX Rate
	 * @param segmentCustomBuilderControl Segment Custom Builder Control
	 * @param tenorArray Tenor Array
	 * @param crossCurrencyBasisArray Cross Currency Basis Array
	 * @param basisOnDerivedLeg TRUE - Basis on Derived Leg
	 * 
	 * @throws Exception Thrown if the Forward Curve Reference Component Basis cannot be calculated
	 */

	public static final void ForwardCurveReferenceComponentBasis (
		final String referenceCurrency,
		final String derivedCurrency,
		final JulianDate valueDate,
		final MergedDiscountForwardCurve referenceDiscountCurve,
		final ForwardCurve reference6MForwardCurve,
		final ForwardCurve reference3MForwardCurve,
		final MergedDiscountForwardCurve derivedDiscountCurve,
		final ForwardCurve derived6MForwardCurve,
		final double referenceToDerivedFX,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final String[] tenorArray,
		final double[] crossCurrencyBasisArray,
		final boolean basisOnDerivedLeg)
		throws Exception
	{
		ComponentPair[] componentPairArray = MakeCCSP (
			valueDate,
			referenceCurrency,
			derivedCurrency,
			tenorArray,
			3,
			referenceToDerivedFX
		);

		CurrencyPair currencyPair = CurrencyPair.FromCode (derivedCurrency + "/" + referenceCurrency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setForwardState (reference3MForwardCurve);

		curveSurfaceQuoteContainer.setForwardState (reference6MForwardCurve);

		curveSurfaceQuoteContainer.setFundingState (referenceDiscountCurve);

		curveSurfaceQuoteContainer.setForwardState (derived6MForwardCurve);

		curveSurfaceQuoteContainer.setFundingState (derivedDiscountCurve);

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				fxLabel.fullyQualifiedName(),
				valueDate,
				currencyPair,
				new String[]
				{
					"10Y"
				},
				new double[]
				{
					referenceToDerivedFX
				},
				referenceToDerivedFX
			)
		);

		curveSurfaceQuoteContainer.setFixing (
			componentPairArray[0].effective(),
			fxLabel,
			referenceToDerivedFX
		);

		ValuationParams valuationParams = new ValuationParams (valueDate, valueDate, referenceCurrency);

		ForwardCurve derived3MForwardCurve = ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			new LinearLatentStateCalibrator (
				segmentCustomBuilderControl,
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[] {
				LatentStateStretchBuilder.ComponentPairForwardStretch (
					"FLOATFLOAT",
					componentPairArray,
					valuationParams,
					curveSurfaceQuoteContainer,
					crossCurrencyBasisArray,
					false,
					basisOnDerivedLeg
				)
			},
			ForwardLabel.Create (
				derivedCurrency,
				"3M"
			),
			valuationParams,
			null,
			MarketParamsBuilder.Create (
				derivedDiscountCurve,
				derived6MForwardCurve,
				null,
				null,
				null,
				null,
				null,
				null
			),
			null,
			derivedDiscountCurve.forward (
				valueDate.julian(),
				valueDate.addTenor ("3M").julian()
			)
		);

		curveSurfaceQuoteContainer.setForwardState (derived3MForwardCurve);

		System.out.println ("\t||----------------------------------------------------------------");

		if (basisOnDerivedLeg) {
			System.out.println ("\t||     RECOVERY OF THE CCBS REFERENCE COMPONENT DERIVED BASIS");
		} else {
			System.out.println ("\t||     RECOVERY OF THE CCBS REFERENCE COMPONENT REFERENCE BASIS");
		}

		System.out.println ("\t||----------------------------------------------------------------");

		for (int componentPairIndex = 0;
			componentPairIndex < componentPairArray.length;
			++componentPairIndex)
		{
			CalibratableComponent derivedComponent =
				componentPairArray[componentPairIndex].derivedComponent();

			System.out.println (
				"\t|| [" + derivedComponent.effectiveDate() + " - " +
					derivedComponent.maturityDate() + "] =>" + FormatUtil.FormatDouble (
						componentPairArray[componentPairIndex].value (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null
						).get (
							basisOnDerivedLeg ? "ReferenceCompDerivedBasis" : "ReferenceCompReferenceBasis"
						),
						1,
						3,
						1.
					) + " | " + FormatUtil.FormatDouble (
						crossCurrencyBasisArray[componentPairIndex],
						1,
						3,
						10000.
					) + " | " + FormatUtil.FormatDouble (
						derived3MForwardCurve.forward (derivedComponent.maturityDate()),
						1,
						4,
						100.
					) + "%"
				);
		}

		IBORCurve.ForwardJack (
			valueDate,
			"---- CCBS DERIVED QUOTE FORWARD CURVE SENSITIVITY ---",
			derived3MForwardCurve,
			"PV"
		);
	}
}
