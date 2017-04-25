@echo off
rem
rem This test script outputs to stdout exactly 1000 lines of text, to test bug #165
rem
FOR /L %%i IN (1,1,10000) DO echo test line %%i