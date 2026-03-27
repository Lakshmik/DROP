
package org.drip.historical.engine;

import java.util.Map;

import org.drip.analytics.cashflow.ComposableUnitFloatingPeriod;
import org.drip.analytics.cashflow.ComposableUnitPeriod;
import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.historical.attribution.PositionMarketSnap;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.Stream;
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
 * <i>FixFloatExplainProcessor</i> contains the Functionality associated with the Horizon Analysis of the Fix
 * 	Float Swap. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>FixFloatExplainProcessor</i> Constructor</li>
 * 		<li>Generate and Snap Relevant Fields from the T1 Market Valuation Parameters</li>
 * 		<li>Update the Fixings (if any) to the Second Market Parameters</li>
 * 		<li>Generate and Snap Relevant Fields from the T2 Market Valuation Parameters</li>
 * 		<li>Generate the Horizon Differential Metrics Map</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/engine/README.md">Product Horizon Change Explain Engine</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixFloatExplainProcessor
	extends HorizonChangeExplainProcessor
{

	/**
	 * <i>FixFloatExplainProcessor</i> Constructor
	 * 
	 * @param fixFloatComponent The Fix Float Component
	 * @param settleLag The Component's Settle Lag
	 * @param marketMeasure The Market Measure Name
	 * @param marketMeasureValue The Market Measure Value
	 * @param t1 First Date
	 * @param t2 Second Date
	 * @param t1CurveSurfaceQuoteContainer First Market Parameters
	 * @param t2CurveSurfaceQuoteContainer Second Market Parameters
	 * @param curveSurfaceQuoteContainerRollDownMap Map of the Roll Down Market Parameters
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public FixFloatExplainProcessor (
		final FixFloatComponent fixFloatComponent,
		final int settleLag,
		final String marketMeasure,
		final double marketMeasureValue,
		final JulianDate t1,
		final JulianDate t2,
		final CurveSurfaceQuoteContainer t1CurveSurfaceQuoteContainer,
		final CurveSurfaceQuoteContainer t2CurveSurfaceQuoteContainer,
		final CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> curveSurfaceQuoteContainerRollDownMap)
		throws Exception
	{
		super (
			fixFloatComponent,
			settleLag,
			marketMeasure,
			marketMeasureValue,
			t1,
			t2,
			t1CurveSurfaceQuoteContainer,
			t2CurveSurfaceQuoteContainer,
			curveSurfaceQuoteContainerRollDownMap
		);
	}

	/**
	 * Generate and Snap Relevant Fields from the T1 Market Valuation Parameters
	 * 
	 * @return The T1 Market Parameters Valuation Snapshot
	 */

	@Override public PositionMarketSnap t1PositionMarketSnap()
	{
		FixFloatComponent fixFloatComponent = (FixFloatComponent) component();

		String payCurrency = fixFloatComponent.payCurrency();

		JulianDate t1 = t1();

		Map<String, Double> fixFloatMeasureMap = fixFloatComponent.value (
			ValuationParams.Spot (t1.addBusDays (settleLag(), payCurrency).julian()),
			null,
			t1MarketParameters(),
			null
		);

		if (null == fixFloatMeasureMap ||
			!fixFloatMeasureMap.containsKey ("Accrued") ||
			!fixFloatMeasureMap.containsKey ("CleanFixedDV01") ||
			!fixFloatMeasureMap.containsKey ("CleanFloatingDV01") ||
			!fixFloatMeasureMap.containsKey ("CleanPV") ||
			!fixFloatMeasureMap.containsKey ("CumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("CumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("DerivedCleanPV") ||
			!fixFloatMeasureMap.containsKey ("DerivedCumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("DerivedCumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("ReferenceCleanPV") ||
			!fixFloatMeasureMap.containsKey ("ReferenceCumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("ReferenceCumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("SwapRate"))
		{
			return null;
		}

		double swapRate = fixFloatMeasureMap.get ("SwapRate");

		MarketMeasureRollDown marketMeasureRollDown = rollDownMeasureMap();

		ForwardLabel forwardLabel = fixFloatComponent.derivedStream().forwardLabel();

		double swapRateSensitivity = 10000. * fixFloatMeasureMap.get ("CleanFixedDV01");

		if (null == marketMeasureRollDown) {
			return null;
		}

		CaseInsensitiveHashMap<Double> horizonMetricMap = marketMeasureRollDown.horizonMetricMap();

		double rollDownInnate = marketMeasureRollDown.innate();

		try {
			PositionMarketSnap positionMarketSnap =
				new PositionMarketSnap (t1, fixFloatMeasureMap.get ("CleanPV"));

			if (!positionMarketSnap.setR1 ("Accrued", fixFloatMeasureMap.get ("Accrued")) ||
				!positionMarketSnap.setR1 ("CleanFixedDV01", swapRateSensitivity) ||
				!positionMarketSnap.setR1 (
					"CleanFloatingDV01",
					10000. * fixFloatMeasureMap.get ("CleanFloatingDV01")
				) ||
				!positionMarketSnap.setC1 ("CouponCurrency", forwardLabel.currency()) ||
				!positionMarketSnap.setR1 (
					"CumulativeCouponAmount",
					fixFloatMeasureMap.get ("CumulativeCouponAmount")
				) ||
				!positionMarketSnap.setR1 (
					"CumulativeCouponDCF",
					fixFloatMeasureMap.get ("CumulativeCouponDCF")
				) ||
				!positionMarketSnap.setR1 ("DerivedCleanPV", fixFloatMeasureMap.get ("DerivedCleanPV")) ||
				!positionMarketSnap.setDate ("EffectiveDate", fixFloatComponent.effectiveDate()) ||
				!positionMarketSnap.setC1 (
					"FixedAccrualDayCount",
					fixFloatComponent.referenceStream().accrualDC()
				) ||
				!positionMarketSnap.setR1 ("FixedCoupon", swapRate) ||
				!positionMarketSnap.setR1 (
					"FixedCumulativeCouponAmount",
					fixFloatMeasureMap.get ("ReferenceCumulativeCouponAmount")
				) ||
				!positionMarketSnap.setR1 (
					"FixedCumulativeCouponDCF",
					fixFloatMeasureMap.get ("ReferenceCumulativeCouponDCF")
				) ||
				!positionMarketSnap.setC1 ("FloatAccrualDayCount", forwardLabel.floaterIndex().dayCount()) ||
				!positionMarketSnap.setR1 (
					"FloatCumulativeCouponAmount",
					fixFloatMeasureMap.get ("DerivedCumulativeCouponAmount")
				) ||
				!positionMarketSnap.setR1 (
					"FloatCumulativeCouponDCF",
					fixFloatMeasureMap.get ("DerivedCumulativeCouponDCF")
				) ||
				!positionMarketSnap.setC1 ("FloaterLabel", forwardLabel.fullyQualifiedName()) ||
				!positionMarketSnap.setDate ("MaturityDate", fixFloatComponent.maturityDate()) ||
				!positionMarketSnap.setC1 ("MaturityTenor", fixFloatComponent.tenor()) ||
				!positionMarketSnap.setC1 ("PayCurrency", payCurrency) ||
				!positionMarketSnap.setR1 (
					"ReferenceCleanPV",
					fixFloatMeasureMap.get ("ReferenceCleanPV")
				) ||
				!positionMarketSnap.setR1 ("SwapRate", swapRate) ||
				!positionMarketSnap.setR1 ("SwapRateRollDown", rollDownInnate)
			)
			{
				return null;
			}

			for (String rollDownTenor : horizonMetricMap.keySet()) {
				if (!positionMarketSnap.setR1 (
					"SwapRateRollDown" + rollDownTenor,
					horizonMetricMap.get (rollDownTenor)
				))
				{
					return null;
				}
			}

			if (!positionMarketSnap.addManifestMeasureSnap (
				"SwapRate",
				swapRate, -1. * swapRateSensitivity,
				rollDownInnate
			))
			{
				return null;
			}

			return positionMarketSnap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Update the Fixings (if any) to the Second Market Parameters
	 * 
	 * @return TRUE - The Fixings were successfully updated to the Second Market Parameters
	 */

	@Override public boolean updateFixings()
	{
		FixFloatComponent fixFloatComponent = (FixFloatComponent) component();

		Stream floatingStream = fixFloatComponent.derivedStream();

		int t2Date = t2().julian();

		if (t2Date > fixFloatComponent.maturityDate().julian()) {
			return false;
		}

		int effectiveDate = fixFloatComponent.effectiveDate().julian();

		if (t2Date <= effectiveDate) {
			t2Date = effectiveDate;
		}

		CompositePeriod fixingCompositePeriod = floatingStream.containingPeriod (t2Date);

		if (null == fixingCompositePeriod) {
			return false;
		}

		ComposableUnitPeriod enclosingComposableUnitPeriod = fixingCompositePeriod.enclosingCUP (t2Date);

		if (null == enclosingComposableUnitPeriod ||
			!(enclosingComposableUnitPeriod instanceof ComposableUnitFloatingPeriod))
		{
			return false;
		}

		CurveSurfaceQuoteContainer t1CurveSurfaceQuoteContainer = t1MarketParameters();

		ForwardLabel forwardLabel = floatingStream.forwardLabel();

		int fixingDate = (
			(ComposableUnitFloatingPeriod) enclosingComposableUnitPeriod
		).referenceIndexPeriod().fixingDate();

		try {
			double resetFixingRate = enclosingComposableUnitPeriod.baseRate (t1CurveSurfaceQuoteContainer);

			return t1CurveSurfaceQuoteContainer.setFixing (fixingDate, forwardLabel, resetFixingRate) &&
				secondMarketParameters().setFixing (fixingDate, forwardLabel, resetFixingRate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Generate and Snap Relevant Fields from the T2 Market Valuation Parameters
	 * 
	 * @return The T2 Market Parameters Valuation Snapshot
	 */

	@Override public PositionMarketSnap t2PositionMarketSnap()
	{
		JulianDate t2 = t2();

		FixFloatComponent fixFloatComponent = (FixFloatComponent) component();

		Map<String, Double> fixFloatMeasureMap = fixFloatComponent.value (
			ValuationParams.Spot (t2.addBusDays (settleLag(), fixFloatComponent.payCurrency()).julian()),
			null,
			secondMarketParameters(),
			null
		);

		if (null == fixFloatMeasureMap ||
			!fixFloatMeasureMap.containsKey ("CleanFixedDV01") ||
			!fixFloatMeasureMap.containsKey ("CleanPV") ||
			!fixFloatMeasureMap.containsKey ("CumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("CumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("DerivedCumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("DerivedCumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("ReferenceCumulativeCouponAmount") ||
			!fixFloatMeasureMap.containsKey ("ReferenceCumulativeCouponDCF") ||
			!fixFloatMeasureMap.containsKey ("ResetDate") ||
			!fixFloatMeasureMap.containsKey ("ResetRate") ||
			!fixFloatMeasureMap.containsKey ("SwapRate"))
		{
			return null;
		}

		double swapRate = fixFloatMeasureMap.get ("SwapRate");

		try {
			PositionMarketSnap positionMarketSnap =
				new PositionMarketSnap (t2, fixFloatMeasureMap.get ("CleanPV"));

			if (!positionMarketSnap.setR1 (
					"CumulativeCouponAmount",
					fixFloatMeasureMap.get ("CumulativeCouponAmount")
				) || !positionMarketSnap.setR1 (
					"CumulativeCouponDCF",
					fixFloatMeasureMap.get ("CumulativeCouponDCF")
				) || !positionMarketSnap.setR1 (
					"FixedCumulativeCouponAmount",
					fixFloatMeasureMap.get ("ReferenceCumulativeCouponAmount")
				) || !positionMarketSnap.setR1 (
					"FixedCumulativeCouponDCF",
					fixFloatMeasureMap.get ("ReferenceCumulativeCouponDCF")
				) || !positionMarketSnap.setR1 (
					"FloatCumulativeCouponAmount",
					fixFloatMeasureMap.get ("DerivedCumulativeCouponAmount")
				) || !positionMarketSnap.setR1 (
					"FloatCumulativeCouponAmount",
					fixFloatMeasureMap.get ("DerivedCumulativeCouponAmount")
				) || !positionMarketSnap.setR1 (
					"FloatCumulativeCouponDCF",
					fixFloatMeasureMap.get ("DerivedCumulativeCouponDCF")
				) || !positionMarketSnap.setDate (
					"ResetDate",
					new JulianDate ((int) (double) fixFloatMeasureMap.get ("ResetDate"))
				) || !positionMarketSnap.setR1 (
					"ResetRate",
					fixFloatMeasureMap.get ("ResetRate")
				) || !positionMarketSnap.setR1 (
					"SwapRate",
					swapRate
				) || !positionMarketSnap.addManifestMeasureSnap (
					"SwapRate",
					swapRate,
					-10000. * fixFloatMeasureMap.get ("CleanFixedDV01"),
					0.
				)
			)
			{
				return null;
			}

			return positionMarketSnap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Horizon Differential Metrics Map
	 * 
	 * @param t1PositionMarketSnap The First Position Market Snap
	 * @param t2PositionMarketSnap The Second Position Market Snap
	 * 
	 * @return The Horizon Differential Metrics Map
	 */

	@Override public CaseInsensitiveHashMap<Double> crossHorizonDifferentialMetrics (
		final PositionMarketSnap t1PositionMarketSnap,
		final PositionMarketSnap t2PositionMarketSnap)
	{
		if (null == t1PositionMarketSnap || null == t2PositionMarketSnap) {
			return null;
		}

		CaseInsensitiveHashMap<Double> differentialMetricMap = new CaseInsensitiveHashMap<Double>();

		String floatAccrualDayCount = t1PositionMarketSnap.c1 ("FloatAccrualDayCount");

		String fixedAccrualDayCount = t1PositionMarketSnap.c1 ("FixedAccrualDayCount");

		JulianDate effectiveDate = t1PositionMarketSnap.date ("EffectiveDate");

		int effectiveDatePlus3M = effectiveDate.addTenor ("3M").julian();

		int effectiveDatePlus1M = effectiveDate.addTenor ("1M").julian();

		String calendar = t1PositionMarketSnap.c1 ("PayCurrency");

		int effectiveDateJulian = effectiveDate.julian();

		try {
			double fixedCumulativeCouponAmount = t2PositionMarketSnap.r1 ("FixedCumulativeCouponAmount") -
				t1PositionMarketSnap.r1 ("FixedCumulativeCouponAmount");

			double fixedCumulativeCouponDCF = t2PositionMarketSnap.r1 ("FixedCumulativeCouponDCF") -
				t1PositionMarketSnap.r1 ("FixedCumulativeCouponDCF");

			double floatCumulativeCouponAmount = t2PositionMarketSnap.r1 ("FloatCumulativeCouponAmount") -
				t1PositionMarketSnap.r1 ("FloatCumulativeCouponAmount");

			double floatCumulativeCouponDCF = t2PositionMarketSnap.r1 ("FloatCumulativeCouponDCF") -
				t1PositionMarketSnap.r1 ("FloatCumulativeCouponDCF");

			differentialMetricMap.put (
				"CumulativeCouponAmount",
				t2PositionMarketSnap.r1 ("CumulativeCouponAmount") -
					t1PositionMarketSnap.r1 ("CumulativeCouponAmount")
			);

			differentialMetricMap.put (
				"CumulativeCouponDCF",
				t2PositionMarketSnap.r1 ("CumulativeCouponDCF") -
					t1PositionMarketSnap.r1 ("CumulativeCouponDCF")
			);

			differentialMetricMap.put (
				"EffectiveFixedCouponRate",
				fixedCumulativeCouponAmount / fixedCumulativeCouponDCF
			);

			differentialMetricMap.put (
				"EffectiveFloatCouponRate",
				floatCumulativeCouponAmount / floatCumulativeCouponDCF
			);

			differentialMetricMap.put (
				"FixedAccrualDCF1M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDatePlus1M,
					fixedAccrualDayCount,
					false,
					null,
					calendar
				)
			);

			differentialMetricMap.put (
				"FixedAccrualDCF3M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDatePlus3M,
					fixedAccrualDayCount,
					false,
					null,
					calendar
				)
			);

			differentialMetricMap.put ("FixedCumulativeCouponAmount", fixedCumulativeCouponAmount);

			differentialMetricMap.put ("FixedCumulativeCouponDCF", fixedCumulativeCouponDCF);

			differentialMetricMap.put (
				"FloatAccrualDCF1M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDatePlus1M,
					floatAccrualDayCount,
					false,
					null,
					calendar
				)
			);

			differentialMetricMap.put (
				"FloatAccrualDCF3M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDatePlus3M,
					floatAccrualDayCount,
					false,
					null,
					calendar
				)
			);

			differentialMetricMap.put ("FloatCumulativeCouponAmount", floatCumulativeCouponAmount);

			differentialMetricMap.put ("FloatCumulativeCouponDCF", floatCumulativeCouponDCF);

			return differentialMetricMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
