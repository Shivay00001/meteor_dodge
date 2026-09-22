"""Smoke test: Android project layout is complete and Kotlin sources parse.

No Kotlin toolchain here; checks layout plus basic structural sanity
(package declaration and balanced braces per file).
"""
from pathlib import Path

ROOT = Path(__file__).parent.parent
EXPECTED_KT = ("MainActivity.kt", "GameView.kt", "GameThread.kt",
               "Meteor.kt", "Player.kt")


def test_gradle_layout():
    assert (ROOT / "settings.gradle.kts").exists()
    assert (ROOT / "build.gradle.kts").exists()
    assert (ROOT / "app" / "build.gradle.kts").exists()


def test_kotlin_sources_present_and_balanced():
    kt_files = {p.name: p for p in (ROOT / "app").rglob("*.kt")}
    missing = [k for k in EXPECTED_KT if k not in kt_files]
    assert not missing, f"missing Kotlin sources: {missing}"
    for name, p in sorted(kt_files.items()):
        src = p.read_text(encoding="utf-8")
        assert "package " in src, f"{name} has no package declaration"
        assert src.count("{") == src.count("}"), f"{name} has unbalanced braces"
