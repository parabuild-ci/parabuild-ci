#!/usr/bin/perl

use warnings;
use strict;

use SOAP::Lite +trace => qw(all);
use Data::Dumper;

my $uri = 'http://localhost:8080/parabuild/integration/webservice/Parabuild';

my $url = 'http://localhost:8080' .
'/parabuild/integration/webservice/Parabuild';

my $client = SOAP::Lite->new(
proxy => [
$url,
credentials => [
'localhost:8080',
'Build Manager',
'admin' => 'admin',
],
],
);

# $client->uri($uri);

my $sver = $client->serverVersion()->result();
print $sver, "\n";

my $foo = $client->getSystemProperties();
print $foo, "\n"; 