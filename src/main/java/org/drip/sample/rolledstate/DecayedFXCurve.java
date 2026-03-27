
package org.drip.sample.rolledstate;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.historical.builder.FXForwardStateCustomization;
import org.drip.historical.builder.FXForwardStateEvaluation;
import org.drip.historical.builder.FXForwardStateGenerator;
import org.drip.historical.calibration.FXForwardInstrumentSuite;
import org.drip.historical.calibration.FXForwardMarkSuite;
import org.drip.product.params.CurrencyPair;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.ResponseScalingShapeControl;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.fx.FXCurve;
import org.drip.state.inference.LinearLatentStateCalibrator;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>DecayedFXCurve</i> illustrates the build out the forward decayed FX Curve.
 *  
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/rolledstate/README.md">Generation of T1 Decayed State</a></li>
 *  </ul>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DecayedFXCurve
{

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

		double fxSpot = 1.0993;
		String fxCode = "USD/EUR";
		String fxForwardCalibrationMetric = "Outright";
		String[] maturityTenorArray =
		{
			"1W",
			"1M",
			"3M",
			"6M",
			"1Y",
			"2Y",
			"3Y"
		};
		double[] fxForwardArray =
		{
			1.1000, //	"1W",
			1.1012,	// 	"1M",
			1.1039,	// 	"3M",
			1.1148,	// 	"6M",
			1.1232,	// 	"1Y",
			1.1497,	// 	"2Y",
			1.1865,	// 	"3Y"
		};

		LinearLatentStateCalibrator linearLatentStateCalibrator = new LinearLatentStateCalibrator (
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
		);

		JulianDate t0 = DateUtil.Today().addTenor ("0D");

		CurrencyPair currencyPair = CurrencyPair.FromCode (fxCode);

		JulianDate t1 = t0.addBusDays (1, currencyPair.denomCcy());

		FXForwardStateGenerator fxForwardStateGenerator = new FXForwardStateGenerator (
			new FXForwardStateCustomization (
				linearLatentStateCalibrator,
				new FXForwardInstrumentSuite (
					currencyPair,
					fxForwardCalibrationMetric,
					maturityTenorArray
				)
			)
		);

		FXForwardStateEvaluation fxForwardStateEvaluation = fxForwardStateGenerator.t0EOD (
			new FXForwardMarkSuite (t0, fxSpot, fxForwardArray)
		);

		FXCurve fxCurve = fxForwardStateEvaluation.fxCurve();

		double[] t1FXForwardQuoteArray = fxForwardStateGenerator.t1SOD (
			t1,
			fxCurve
		).markSuite().quoteArray();

		System.out.println ("\n\t|-------------------------------------------------------------------||");

		System.out.println ("\t|    Custom FX Curve Builder Metrics                                ||");

		System.out.println ("\t|-------------------------------------------------------------------||");

		System.out.println ("\t|    L -> R:                                                        ||");

		System.out.println ("\t|        FX Forward Tenor                                           ||");

		System.out.println ("\t|        Input FX Forward Outright                                  ||");

		System.out.println ("\t|        Curve FX Forward Outright                                  ||");

		System.out.println ("\t|-------------------------------------------------------------------||");

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			System.out.println (
				"\t| [" + maturityTenorArray[tenorIndex] + "] => " +
				FormatUtil.FormatDouble (fxForwardArray[tenorIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fxCurve.fx (maturityTenorArray[tenorIndex]), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (t1FXForwardQuoteArray[tenorIndex], 1, 6, 1.) + " ||"
			);
		}

		System.out.println ("\t|-------------------------------------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
