'use client';

import { useEffect } from 'react';
import { getDeviceId } from '@/lib/deviceId';

/**
 * Client component that initializes Device ID on app mount
 * This ensures Device ID is generated and stored in LocalStorage
 * as soon as the user visits the app
 */
export default function DeviceIdInitializer() {
  useEffect(() => {
    // Initialize Device ID on mount
    const deviceId = getDeviceId();

    if (deviceId) {
      console.log('Device ID initialized:', deviceId);
    }
  }, []);

  // This component doesn't render anything
  return null;
}
