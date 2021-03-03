package com.coach.flame.testing.suites

import org.junit.platform.runner.JUnitPlatform
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.SuiteDisplayName
import org.junit.runner.RunWith

@SuiteDisplayName("Flame Coach Component Testing")
@SelectPackages("com.coach.flame.testing.component")
@RunWith(JUnitPlatform::class)
class ComponentTestingSuite